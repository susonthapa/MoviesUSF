package np.com.susonthapa.moviesusf.presentation.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.core.Observable
import np.com.susonthapa.core.ui.common.UBaseFragment
import np.com.susonthapa.moviesusf.BaseApplication
import np.com.susonthapa.moviesusf.R
import np.com.susonthapa.moviesusf.databinding.FragmentHomeBinding
import np.com.susonthapa.moviesusf.di.ViewModelFactory
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
        initializeReducer(viewModel)
    }

    override fun render(state: HomeState) {
        state.searchResult?.getValueIfChanged()?.let {
            Timber.d("------ rendering search results: $it")
        }

        state.history.getValueIfChanged()?.let {
            Timber.d("------ rendering history: $it")
        }
    }

    override fun trigger(effect: HomeEffects) {
        when (effect) {

        }
    }

    override fun onResume() {
        super.onResume()
        val screenLoadEvent: Observable<ScreenLoadEvent> = Observable.just(ScreenLoadEvent)

        uiDisposable = Observable.merge(
            arrayListOf(
                screenLoadEvent,
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