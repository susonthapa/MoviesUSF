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
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialElevationScale
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import np.com.susonthapa.moviesusf.presentation.usf.UBaseFragment
import np.com.susonthapa.moviesusf.BaseApplication
import np.com.susonthapa.moviesusf.databinding.FragmentHomeBinding
import np.com.susonthapa.moviesusf.di.ViewModelFactory
import np.com.susonthapa.moviesusf.domain.DataStatus
import timber.log.Timber
import javax.inject.Inject
import np.com.susonthapa.moviesusf.presentation.home.HomeEvents.*
import np.com.susonthapa.moviesusf.presentation.home.HomeEffects.*

class HomeFragment : UBaseFragment<HomeEvents, HomeState, HomeEffects>() {

    private val binding: FragmentHomeBinding
        get() = _binding!! as FragmentHomeBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: HomeViewModel

    private lateinit var adapter: SearchResultAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var sharedView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
        // initialize the adapter here to restore the scroll positions when this fragment is popped from the backstack
        // as this method is only called once for the lifetime of the fragment and this will insure that the recyclerview
        // will have some data to display
        // Note: This will cause memory leak if all the references to this adapter is not cleared in onDestroyView
        adapter = SearchResultAdapter(object : SearchResultClickListener {

            override fun onResultClick(position: Int, sharedView: View) {
                this@HomeFragment.sharedView = sharedView
                generalEvents.accept(LoadMovieDetailsEvent(position))
            }

            override fun onAddToHistory(position: Int) {
                generalEvents.accept(AddMovieToHistoryEvent(position))
            }

        })
        historyAdapter = HistoryAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // postpone the enter transition
        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
        binding.searchResultList.recyclerView.adapter = adapter
        binding.moviesHistory.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.moviesHistory.adapter = historyAdapter
        // check for history items and show the history
        // this will be true when the fragment is popped from the backstack and it had movies in the history
        // Not showing history will cause the searchResult recycler view to take the full width and will
        // interfere with the sharedElement transition when this fragment is popped from the backstack
        if (historyAdapter.itemCount != 0) {
            showHistory()
        }
        initializeReducer(viewModel)
    }

    override fun render(state: HomeState) {
        state.history.getValueIfChanged()?.let {
            Timber.d("------ rendering history: $it")
            historyAdapter.submitList(it)
            if (it.isNotEmpty()) {
                showHistory()
            } else {
                hideHistory()
            }
        }

        state.searchResult.getValueIfChanged()?.let {
            Timber.d("------ rendering search results: $it")
            adapter.submitList(it)
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
    }

    override fun trigger(effect: HomeEffects) {
        Timber.d("------ triggering effect: $effect")
        when (effect) {
            is NavigateToDetailsEffect -> {
                val extras = FragmentNavigatorExtras(sharedView to effect.movie.id)
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
                        effect.movie, effect.movie.id
                    ), extras
                )
            }

            is NavigateToHistoryEffect -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToHistoryFragment(
                        effect.movies.toTypedArray()
                    )
                )
            }
        }
    }

    private fun showHistory() {
        binding.apply {
            homeHistoryViewAll.visibility = View.VISIBLE
            homeMovieHistoryLabel.visibility = View.VISIBLE
            binding.moviesHistory.visibility = View.VISIBLE
        }
    }

    private fun hideHistory() {
        binding.apply {
            homeHistoryViewAll.visibility = View.GONE
            homeMovieHistoryLabel.visibility = View.GONE
            binding.moviesHistory.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        val screenLoadEvent: Observable<ScreenLoadEvent> =
            Observable.just(ScreenLoadEvent(isRestoredFromBackStack))
        val searchMovieEvent: Observable<SearchMovieEvent> = binding.searchButton.clicks()
            .map {
                SearchMovieEvent(binding.searchEditText.text.toString())
            }
        val viewHistoryEvent: Observable<ViewHistoryEvent> = binding.homeHistoryViewAll.clicks()
            .map {
                ViewHistoryEvent
            }

        uiDisposable = Observable.merge(
            arrayListOf(
                screenLoadEvent,
                searchMovieEvent,
                viewHistoryEvent,
                generalEvents
            )
        ).subscribe({
            viewModel.processEvent(it)
        }, {
            Timber.e(it, "error in processing events")
        })
    }

    override fun onDestroyView() {
        // reset the recycler view
        binding.searchResultList.recyclerView.adapter = null
        binding.moviesHistory.adapter = null
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as BaseApplication).appComponent.inject(this)
    }

}