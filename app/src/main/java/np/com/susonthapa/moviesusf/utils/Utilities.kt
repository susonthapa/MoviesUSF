package np.com.susonthapa.moviesusf.utils

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.functions.BiFunction

/**
 * Created by suson on 8/2/20
 */

inline fun <T, U, R> Observable<T>.withLatestFrom(
    other: ObservableSource<U>,
    crossinline combiner: (T, U) -> R
): Observable<R> = withLatestFrom(other, BiFunction<T, U, R> { t, u -> combiner.invoke(t, u) })
