package np.com.susonthapa.moviesusf.presentation.home

import com.airbnb.mvrx.MavericksState
import np.com.susonthapa.moviesusf.data.ViewVisibility
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.domain.Movies

/**
 * Created by suson on 8/1/20
 */


/**
 * State to represent the Home screen
 */
data class HomeState(
    val searchResult: List<Movies> = listOf(),
    val searchStatus: ContentStatus = ContentStatus.LOADED,
    val history: List<Movies> = listOf(),
    val searchAnimation: ViewVisibility = ViewVisibility(),
    val oldState: HomeState? = null
) : MavericksState {


    val dSearchResult: List<Movies>?
        get() = if (searchResult == oldState?.searchResult) null else searchResult

    val dSearchStatus: ContentStatus?
        get() = if (searchStatus == oldState?.searchStatus) null else searchStatus

    val dSearchAnimation: ViewVisibility?
        get() = if (searchAnimation == oldState?.searchAnimation) null else searchAnimation

    val dHistory: List<Movies>?
        get() = if (history == oldState?.history)  null else history

    fun diffCopy(
        searchResult: List<Movies> = this.searchResult,
        searchStatus: ContentStatus = this.searchStatus,
        history: List<Movies> = this.history,
        searchAnimation: ViewVisibility = this.searchAnimation,
        oldState: HomeState? = this
    ): HomeState {
        return HomeState(
            searchResult,
            searchStatus,
            history,
            searchAnimation,
            oldState?.copy(oldState = null)
        )
    }
}
