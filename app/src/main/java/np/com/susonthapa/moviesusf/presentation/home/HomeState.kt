package np.com.susonthapa.moviesusf.presentation.home

import np.com.susonthapa.moviesusf.data.Lce
import np.com.susonthapa.moviesusf.data.ViewBox
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.domain.Movies
import np.com.susonthapa.moviesusf.presentation.usf.Effect
import np.com.susonthapa.moviesusf.presentation.usf.Event
import np.com.susonthapa.moviesusf.presentation.usf.Result
import np.com.susonthapa.moviesusf.presentation.usf.State

/**
 * Created by suson on 8/1/20
 */


/**
 * State to represent the Home screen
 */
data class HomeState(
    val searchResult: ViewBox<List<Movies>> = ViewBox(listOf()),
    val searchStatus: ViewBox<ContentStatus> = ViewBox(ContentStatus.LOADED),
    val history: ViewBox<List<Movies>> = ViewBox(listOf())
) : State {

    fun stateCopy(
        searchResult: ViewBox<List<Movies>> = this.searchResult.stateCopy(),
        searchStatus: ViewBox<ContentStatus> = this.searchStatus.stateCopy(),
        history: ViewBox<List<Movies>> = this.history.stateCopy()
    ) = HomeState(searchResult, searchStatus, history)

    fun resetCopy(
        searchResult: ViewBox<List<Movies>> = this.searchResult.resetCopy(),
        searchStatus: ViewBox<ContentStatus> = this.searchStatus.resetCopy(),
        history: ViewBox<List<Movies>> = this.history.resetCopy()
    ) = HomeState(searchResult, searchStatus, history)

}

sealed class HomeEvents : Event {
    object ScreenLoadEvent : HomeEvents()
    data class SearchMovieEvent(val query: String) : HomeEvents()
    data class AddMovieToHistoryEvent(val position: Int) : HomeEvents()
    data class LoadMovieDetailsEvent(val position: Int) : HomeEvents()
}

sealed class HomeEffects : Effect {
    data class ShowMessageEffect(val message: String) : HomeEffects()
    data class NavigateToDetailsEffect(val movie: Movies) : HomeEffects()
    object NoEffect : HomeEffects()
}

sealed class HomeResults : Result {
    object ScreenLoadResult : HomeResults()
    data class SearchMovieResult(val movies: List<Movies>) : HomeResults()
    data class SearchMovieStatusResult(val status: ContentStatus) : HomeResults()
    data class AddMovieToHistoryResult(val history: List<Movies>) : HomeResults()
    data class LoadMovieDetailsResult(val movie: Movies) : HomeResults()
    object NoResult : HomeResults()
}
