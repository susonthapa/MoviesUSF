package np.com.susonthapa.moviesusf.presentation.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.core.Observable
import np.com.susonthapa.moviesusf.BaseApplication
import np.com.susonthapa.moviesusf.databinding.FragmentHistoryBinding
import np.com.susonthapa.moviesusf.di.ViewModelFactory
import np.com.susonthapa.moviesusf.presentation.usf.UBaseFragment
import javax.inject.Inject
import np.com.susonthapa.moviesusf.presentation.history.HistoryEvents.*
import np.com.susonthapa.moviesusf.presentation.history.HistoryEffects.*
import timber.log.Timber

/**
 * Created by suson on 8/20/20
 */

class HistoryFragment : UBaseFragment<HistoryEvents, HistoryState, HistoryEffects>() {

    private val binding: FragmentHistoryBinding
        get() = _binding!! as FragmentHistoryBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryListAdapter

    private val args: HistoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this, viewModelFactory)[HistoryViewModel::class.java]
        initializeReducer(viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyAdapter = HistoryListAdapter {
            generalEvents.accept(RemoveMovieEvent(it))
        }
        binding.historyList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }


    override fun render(state: HistoryState) {
        state.movies.getValueIfChanged()?.let {
            Timber.d("rendering movies list: $it")
            historyAdapter.submitList(it)
        }
    }

    override fun trigger(effect: HistoryEffects) {
        when (effect) {
            is ShowMessageEffect -> {
                showLongFeedBack(effect.message)
            }
            
            is NavigateBackEffect -> {
                Timber.d("------ navigating back")
                findNavController().navigateUp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val screenLoadEvent: Observable<ScreenLoadEvent> = Observable.just(ScreenLoadEvent(args.movies.toList()))

        uiDisposable = Observable.merge(
            arrayListOf(
                screenLoadEvent,
                generalEvents
            )
        ).subscribe({
            viewModel.processEvent(it)
        }, {
            it.printStackTrace()
            Timber.e(it, "failed to process the event")
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as BaseApplication).appComponent.inject(this)
    }

}