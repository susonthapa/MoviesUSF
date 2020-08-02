package np.com.susonthapa.moviesusf.presentation.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        initializeReducer(viewModel)
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
        val screenLoadEvent: Observable<ScreenLoadEvent> = Observable.just(ScreenLoadEvent)
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
            viewModel.processEvent(it)
        }, {
            Timber.e(it, "error in processing events")
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as BaseApplication).appComponent.inject(this)
    }

}