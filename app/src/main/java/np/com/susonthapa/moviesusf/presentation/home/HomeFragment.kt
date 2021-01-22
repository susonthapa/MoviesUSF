package np.com.susonthapa.moviesusf.presentation.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.withState
import np.com.susonthapa.moviesusf.BaseApplication
import np.com.susonthapa.moviesusf.databinding.FragmentHomeBinding
import np.com.susonthapa.moviesusf.di.ViewModelFactory
import np.com.susonthapa.moviesusf.domain.DataStatus
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : BaseMvRxFragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding: FragmentHomeBinding
        get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: HomeViewModel

    private lateinit var adapter: SearchResultAdapter
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        viewModel.subscribe(this, subscriber = {postInvalidate()})

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SearchResultAdapter(object: SearchResultClickListener {
            override fun onResultClick(position: Int) {
                viewModel.loadMovieDetails(position)
            }

            override fun onAddToHistory(position: Int) {
                viewModel.addMovieToHistory(position)
            }

        })
        binding.searchResultList.recyclerView.adapter = adapter
        historyAdapter = HistoryAdapter()
        binding.moviesHistory.adapter = historyAdapter
        binding.moviesHistory.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        setupObservers()
        setupEventListeners()
    }

    override fun invalidate() {
        withState(viewModel) {
            Timber.d("------ rendering state: $it")
            adapter.submitList(it.searchResult)
            historyAdapter.submitList(it.history)
            binding.moviesHistory.isVisible = it.history.isNotEmpty()
            binding.searchButton.isEnabled = it.searchStatus.status != DataStatus.LOADING

            when (it.searchStatus.status) {
                DataStatus.LOADING -> {
                    binding.searchIndicator.visibility = View.VISIBLE
                }

                DataStatus.LOADED -> {
                    binding.searchIndicator.visibility = View.GONE
                    binding.searchResultList.hideAllViews()
                }

                DataStatus.EMPTY -> {
                    binding.searchIndicator.visibility = View.GONE
                    binding.searchResultList.showEmptyView()
                }

                DataStatus.ERROR -> {
                    binding.searchIndicator.visibility = View.GONE
                    binding.searchResultList.showErrorView(it.searchStatus.msg)
                }
            }

            if (it.searchAnimation.isAnimated) {
                val scaleFactor = 0.2f
                val growX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f + scaleFactor)
                val growY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f + scaleFactor)
                val growAnimation = ObjectAnimator.ofPropertyValuesHolder(binding.searchButton, growX, growY)
                growAnimation.interpolator = OvershootInterpolator()

                val shrinkX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
                val shrinkY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
                val shrinkAnimation = ObjectAnimator.ofPropertyValuesHolder(binding.searchButton, shrinkX, shrinkY)
                shrinkAnimation.interpolator = OvershootInterpolator()

                val animSet = AnimatorSet()
                animSet.playSequentially(growAnimation, shrinkAnimation)
                animSet.start()
            }
        }
    }

    private fun setupObservers() {
        viewModel.navigateToMovieDetails.observe(viewLifecycleOwner, {
            findNavController().navigate(HomeFragmentDirections.actionMHomeFragmentToDetailsFragment(it))
        })
    }

    private fun setupEventListeners() {
        binding.searchButton.setOnClickListener {
            viewModel.searchMovie(binding.searchEditText.text.toString())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as BaseApplication).appComponent.inject(this)
    }

}