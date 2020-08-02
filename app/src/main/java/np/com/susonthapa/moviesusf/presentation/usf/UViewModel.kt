package np.com.susonthapa.moviesusf.presentation.usf

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import np.com.susonthapa.core.ui.common.DisposingViewModel
import timber.log.Timber

/**
 * Created by suson on 7/7/20
 */
abstract class UViewModel<E : Event, R : Result, S : State, F : Effect> : DisposingViewModel(),
    Reducer<E, R, S, F> {

    override val state: Observable<S>
    override val effect: Observable<F>

    protected val mState: Observable<S>

    private val eventEmitter = PublishRelay.create<E>()

    init {
        val results = eventToResult(
            eventEmitter
                .doOnNext {
                    Timber.d("------ events: $it")
                }
        )
            .doOnNext {
                Timber.d("------ $it")
            }

        results
            .publish()
            .autoConnect(1) {
                addDisposable(it)
             }
            .also {o ->
                mState = resultToState(o)
                    .doOnNext {
                        Timber.d("------ vs: $it")
                    }
                    .replay(1)
                    .autoConnect(1) {
                        addDisposable(it)
                    }

                state = mState.skip(1)

                effect = resultToEffect(o)
                    .doOnNext {
                        Timber.d("------ ve: $it")
                    }
            }
    }

    fun processEvent(events: E) {
        eventEmitter.accept(events)
    }
}