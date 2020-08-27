package np.com.susonthapa.moviesusf.presentation.history

import np.com.susonthapa.moviesusf.data.ViewBox
import np.com.susonthapa.moviesusf.domain.Movies
import np.com.susonthapa.moviesusf.presentation.usf.Effect
import np.com.susonthapa.moviesusf.presentation.usf.Event
import np.com.susonthapa.moviesusf.presentation.usf.Result
import np.com.susonthapa.moviesusf.presentation.usf.State

/**
 * Created by suson on 8/20/20
 */

data class HistoryState(
    val movies: ViewBox<List<Movies>> =  ViewBox(listOf())
) : State {

    fun stateCopy(
        movies: ViewBox<List<Movies>> = this.movies.stateCopy()
    ) = HistoryState(movies)

    fun resetCopy(
        movies: ViewBox<List<Movies>> = this.movies.resetCopy()
    ) = HistoryState(movies)

}

sealed class HistoryEvents : Event {
    data class ScreenLoadEvent(val movies: List<Movies>) : HistoryEvents()
    data class RemoveMovieEvent(val position: Int) : HistoryEvents()
}

sealed class HistoryEffects : Effect {
    data class ShowMessageEffect(val message: String) : HistoryEffects()
    object NavigateBackEffect : HistoryEffects()
    object NoEffect : HistoryEffects()
}

sealed class HistoryResults : Result {
    data class ScreenLoadResult(val movies: List<Movies>) : HistoryResults()
    data class RemoveMovieResult(val movies: List<Movies>) : HistoryResults()
    data class ShowMessageResult(val message: String) : HistoryResults()
    object NavigateBackResult : HistoryResults()
    object NoResult : HistoryResults()
}



