package np.com.susonthapa.moviesusf.presentation.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import np.com.susonthapa.moviesusf.databinding.FragmentHomeBinding
import np.com.susonthapa.moviesusf.domain.DataStatus
import np.com.susonthapa.moviesusf.presentation.home.HomeEffects.NavigateToDetailsEffect
import np.com.susonthapa.moviesusf.presentation.home.HomeEvents.*
import np.com.susonthapa.moviesusf.presentation.usf.UBaseFragment
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : UBaseFragment<HomeEvents, HomeState, HomeEffects>() {

    private val binding: FragmentHomeBinding
        get() = _binding!! as FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModel()

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
        adapter = SearchResultAdapter(object: SearchResultClickListener {

            override fun onResultClick(position: Int) {
                generalEvents.accept(LoadMovieDetailsEvent(position))
            }

            override fun onAddToHistory(position: Int) {
                generalEvents.accept(AddMovieToHistoryEvent(position))
            }

        })
        binding.searchResultList.recyclerView.adapter = adapter
        historyAdapter = HistoryAdapter()
        binding.moviesHistory.adapter = historyAdapter
        binding.moviesHistory.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        initializeReducer(homeViewModel)
    }

    override fun render(state: HomeState) {
        Timber.d("------ rendering state: $state")
        state.searchResult.getValueIfChanged()?.let {
            Timber.d("------ rendering search results: $it")
            adapter.submitList(it)
        }

        state.history.getValueIfChanged()?.let {
            Timber.d("------ rendering history: $it")
            historyAdapter.submitList(it)
            if (it.isNotEmpty()) {
                binding.moviesHistory.visibility = View.VISIBLE
            } else {
                binding.moviesHistory.visibility = View.GONE
            }
        }

        state.searchStatus.getValueIfChanged()?.let {
            Timber.d("------ rendering search result status: $it")
            when (it.status) {
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
                    binding.searchResultList.showErrorView(it.msg)
                }

            }
        }

        state.searchAnimation.getValueIfChanged()?.let {
            Timber.d("------ rendering animation of search: $it")
            if (it.isAnimated) {
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

    override fun trigger(effect: HomeEffects) {
        Timber.d("------ triggering effect: $effect")
        when (effect) {
            is NavigateToDetailsEffect -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment(effect.movie))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val screenLoadEvent: Observable<ScreenLoadEvent> = Observable.just(ScreenLoadEvent(isRestoredFromBackStack))
        val searchMovieEvent: Observable<SearchMovieEvent> = binding.searchButton.clicks()
            .map {
                SearchMovieEvent(binding.searchEditText.text.toString())
            }

        uiDisposable = Observable.merge(
            arrayListOf(
                screenLoadEvent,
                searchMovieEvent,
                generalEvents
            )
        ).subscribe({
            homeViewModel.processEvent(it)
        }, {
            Timber.e(it, "error in processing events")
        })
    }

}