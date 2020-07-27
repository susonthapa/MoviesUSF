package np.com.susonthapa.moviesusf.presentation.usf

import io.reactivex.rxjava3.core.Observable

/**
 * Created by suson on 7/7/20
 */

interface Event
interface State
interface Result
interface Effect

interface Reducer<E: Event, R: Result, S: State, F: Effect> {

    val state: Observable<S>
    val effect: Observable<F>

    fun eventToResult(events: Observable<E>): Observable<out R>
    fun resultToState(results: Observable<out R>): Observable<S>
    fun resultToEffect(results: Observable<out R>): Observable<F>
}

interface UsfView<E: Event, S: State, F: Effect> {
    fun render(state: S)
    fun trigger(effect: F)
}
