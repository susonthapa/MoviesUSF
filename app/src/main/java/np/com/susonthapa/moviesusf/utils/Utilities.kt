package np.com.susonthapa.moviesusf.utils

import android.content.Context
import androidx.annotation.AttrRes
import androidx.core.content.res.use
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.functions.BiFunction
import np.com.susonthapa.moviesusf.presentation.usf.StateBox

/**
 * Created by suson on 8/2/20
 */

inline fun <T, U, R> Observable<T>.withLatestFrom(
    other: ObservableSource<U>,
    crossinline combiner: (T, U) -> R
): Observable<R> = withLatestFrom(other, BiFunction { t, u -> combiner.invoke(t, u) })

fun <T, U, R: StateBox<U, T>> Observable<T>.withLatestStateBox(
    other: ObservableSource<U>
): Observable<R> =
    withLatestFrom(other, BiFunction { t, u ->
        StateBox<U, T>(u, t) as R
    })

fun Context.themeInt(@AttrRes attr: Int): Int {
    return obtainStyledAttributes(intArrayOf(attr)).use {
        it.getInt(0, 0)
    }
}
