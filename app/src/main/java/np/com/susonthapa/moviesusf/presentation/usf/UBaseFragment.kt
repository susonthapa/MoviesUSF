package np.com.susonthapa.moviesusf.presentation.usf

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import np.com.susonthapa.core.ui.common.BaseFragment
import timber.log.Timber

/**
 * Created by suson on 7/7/20
 */

abstract class UBaseFragment<E : Event, S : State, F : Effect> : BaseFragment(),
    UsfView<E, S, F> {
    protected val generalEvents = PublishRelay.create<E>()

    protected fun <R : Result> initializeReducer(reducer: Reducer<E, R, S, F>) {
        bag.add(
            reducer.state
                .doOnNext {
                    Timber.d("------ VS: $it")
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    render(it)
                }, {
                    it.printStackTrace()
                    Timber.e(it, "something just blew up in view effects")
                })
        )

        bag.add(
            reducer.effect
                .doOnNext {
                    Timber.d("------ VE: $it")
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    trigger(it)
                }, {
                    it.printStackTrace()
                    Timber.e(it, "something just blew up in view effects")
                })
        )
    }
}
