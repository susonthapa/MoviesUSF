package np.com.susonthapa.moviesusf.presentation.home

import np.com.susonthapa.moviesusf.data.Lce
import np.com.susonthapa.moviesusf.data.ViewBox
import np.com.susonthapa.moviesusf.data.ViewVisibility
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
    val searchSuggestions: ViewBox<List<String>> = ViewBox(listOf()),
    val suggestionStatus: ViewBox<ContentStatus> = ViewBox(ContentStatus.NONE),
    val searchStatus: ViewBox<ContentStatus> = ViewBox(ContentStatus.NONE),
    val history: ViewBox<List<Movies>> = ViewBox(listOf()),
    val searchAnimation: ViewBox<ViewVisibility> = ViewBox(ViewVisibility())
) : State {

    fun stateCopy(
        searchResult: ViewBox<List<Movies>> = this.searchResult.stateCopy(),
        searchSuggestions: ViewBox<List<String>> = this.searchSuggestions.stateCopy(),
        suggestionStatus: ViewBox<ContentStatus> = this.suggestionStatus.stateCopy(),
        searchStatus: ViewBox<ContentStatus> = this.searchStatus.stateCopy(),
        history: ViewBox<List<Movies>> = this.history.stateCopy(),
        searchAnimation: ViewBox<ViewVisibility> = this.searchAnimation.stateCopy()
    ) = HomeState(searchResult, searchSuggestions, suggestionStatus, searchStatus, history, searchAnimation)

    fun resetCopy(
        searchResult: ViewBox<List<Movies>> = this.searchResult.resetCopy(),
        searchSuggestions: ViewBox<List<String>> = this.searchSuggestions.resetCopy(),
        suggestionStatus: ViewBox<ContentStatus> = this.suggestionStatus.resetCopy(),
        searchStatus: ViewBox<ContentStatus> = this.searchStatus.resetCopy(),
        history: ViewBox<List<Movies>> = this.history.resetCopy(),
        searchAnimation: ViewBox<ViewVisibility> = this.searchAnimation.resetCopy()
    ) = HomeState(searchResult, searchSuggestions, suggestionStatus, searchStatus, history, searchAnimation)

}

sealed class HomeEvents : Event {
    data class ScreenLoadEvent(val isRestored: Boolean) : HomeEvents()
    data class SearchMovieEvent(val query: String) : HomeEvents()
    data class MovieTypingEvent(val query: String) : HomeEvents()
    data class AddMovieToHistoryEvent(val position: Int) : HomeEvents()
    data class LoadMovieDetailsEvent(val position: Int) : HomeEvents()
    object ViewHistoryEvent : HomeEvents()
}

sealed class HomeEffects : Effect {
    data class ShowMessageEffect(val message: String) : HomeEffects()
    data class NavigateToDetailsEffect(val movie: Movies) : HomeEffects()
    data class NavigateToHistoryEffect(val movies: List<Movies>) : HomeEffects()
    object NoEffect : HomeEffects()
}

sealed class HomeResults : Result {
    data class ScreenLoadResult(val isRestored: Boolean) : HomeResults()
    data class SearchMovieResult(val movies: List<Movies>) : HomeResults()
    data class LoadSuggestionResult(val suggestions: List<String>) : HomeResults()
    data class SearchMovieStatusResult(val status: ContentStatus) : HomeResults()
    data class SuggestionStatusResult(val status: ContentStatus) : HomeResults()
    data class AddMovieToHistoryResult(val history: List<Movies>) : HomeResults()
    data class LoadMovieDetailsResult(val movie: Movies) : HomeResults()
    data class ViewHistoryResult(val movies: List<Movies>) : HomeResults()
    object NoResult : HomeResults()
}
