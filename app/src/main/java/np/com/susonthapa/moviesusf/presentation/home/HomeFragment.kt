package np.com.susonthapa.moviesusf.presentation.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import np.com.susonthapa.core.ui.common.BaseFragment
import np.com.susonthapa.moviesusf.presentation.usf.UBaseFragment
import np.com.susonthapa.moviesusf.BaseApplication
import np.com.susonthapa.moviesusf.databinding.FragmentHomeBinding
import np.com.susonthapa.moviesusf.di.ViewModelFactory
import np.com.susonthapa.moviesusf.domain.DataStatus
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : BaseFragment(), MavericksView {

    private val binding: FragmentHomeBinding
        get() = _binding!! as FragmentHomeBinding

    private val viewModel: HomeViewModel by fragmentViewModel()

    private lateinit var adapter: SearchResultAdapter
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SearchResultAdapter(object : SearchResultClickListener {

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
        binding.moviesHistory.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        viewModel.screenLoad(isRestoredFromBackStack)

        binding.searchButton.setOnClickListener {
            viewModel.searchMovie(binding.searchEditText.text.toString())
        }

        viewModel.navigateToDetails.observe(viewLifecycleOwner, {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
                    it
                )
            )
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun invalidate() = withState(viewModel) { state ->
        Timber.d("state: $state")

        state.dSearchStatus?.let {
            Timber.d("------ rendering contentStatus: $it")
            when (it.status) {
                DataStatus.LOADING -> {
                    binding.searchIndicator.isVisible = true
                }

                DataStatus.LOADED -> {
                    binding.searchIndicator.isVisible = false
                    binding.searchResultList.hideAllViews()
                }

                DataStatus.EMPTY -> {
                    binding.searchIndicator.isVisible = false
                    binding.searchResultList.showEmptyView()
                }

                DataStatus.ERROR -> {
                    binding.searchIndicator.isVisible = false
                    binding.searchResultList.showErrorView()
                }
            }
        }

        state.dSearchResult?.let {
            Timber.d("------ rendering searchResult: $it")
            adapter.submitList(state.searchResult)
        }

        state.dHistory?.let {
            Timber.d("------ rendering history: $it")
            historyAdapter.submitList(it)
            binding.moviesHistory.isVisible = it.isNotEmpty()
        }

        state.dSearchAnimation?.let {
            Timber.d("------ rendering animation of search: $it")
            if (it.isAnimated) {
                val scaleFactor = 0.2f
                val growX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f + scaleFactor)
                val growY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f + scaleFactor)
                val growAnimation =
                    ObjectAnimator.ofPropertyValuesHolder(binding.searchButton, growX, growY)
                growAnimation.interpolator = OvershootInterpolator()

                val shrinkX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
                val shrinkY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
                val shrinkAnimation =
                    ObjectAnimator.ofPropertyValuesHolder(binding.searchButton, shrinkX, shrinkY)
                shrinkAnimation.interpolator = OvershootInterpolator()

                val animSet = AnimatorSet()
                animSet.playSequentially(growAnimation, shrinkAnimation)
                animSet.start()
            }
        }

        Unit
    }

}