package np.com.susonthapa.moviesusf.presentation.history

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import np.com.susonthapa.moviesusf.data.ViewBox
import np.com.susonthapa.moviesusf.presentation.usf.UViewModel
import np.com.susonthapa.moviesusf.presentation.history.HistoryEffects.*
import np.com.susonthapa.moviesusf.presentation.history.HistoryEvents.*
import np.com.susonthapa.moviesusf.presentation.history.HistoryResults.*
import np.com.susonthapa.moviesusf.utils.withLatestStateBox

/**
 * Created by suson on 8/20/20
 */
class HistoryViewModel : UViewModel<HistoryEvents, HistoryResults, HistoryState, HistoryEffects>() {

    override fun eventToResult(events: Observable<HistoryEvents>): Observable<out HistoryResults> {
        return events.publish { o ->
            Observable.merge(
                arrayListOf(
                    o.ofType(ScreenLoadEvent::class.java).map { ScreenLoadResult(it.movies) },
                    o.ofType(RemoveMovieEvent::class.java).compose(removeMovie())
                )
            )
        }
    }

    override fun resultToState(results: Observable<out HistoryResults>): Observable<HistoryState> {
        return results.scan(HistoryState()) {vs, result ->
            when (result) {
                is ScreenLoadResult -> {
                    vs.stateCopy(movies = ViewBox(result.movies))
                }

                is RemoveMovieResult -> {
                    vs.stateCopy(movies = ViewBox(result.movies))
                }

                else -> {
                    vs
                }
            }
        }.distinctUntilChanged()
    }

    override fun resultToEffect(results: Observable<out HistoryResults>): Observable<HistoryEffects> {
        return results.map { result ->
            when (result) {
                is ShowMessageResult -> {
                    ShowMessageEffect(result.message)
                }

                is NavigateBackResult -> {
                    NavigateBackEffect
                }

                else -> {
                    NoEffect
                }
            }
        }
    }

    private fun removeMovie(): ObservableTransformer<RemoveMovieEvent, out HistoryResults> {
        return ObservableTransformer { upstream ->
            upstream.withLatestStateBox(mState)
                .map { s  ->
                    val updatedMovies = s.state.movies.value.toMutableList()
                    updatedMovies.removeAt(s.event.position)
                    if (updatedMovies.isEmpty()) {
                        NavigateBackResult
                    } else {
                        RemoveMovieResult(updatedMovies)
                    }
                }
        }
    }

}