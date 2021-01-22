package np.com.susonthapa.moviesusf.presentation.home

import com.airbnb.mvrx.MvRxState
import np.com.susonthapa.moviesusf.data.ViewVisibility
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.domain.Movies

/**
 * Created by suson on 8/1/20
 */

data class HomeState(
    val searchResult: List<Movies> = listOf(),
    val searchStatus: ContentStatus = ContentStatus.LOADED,
    val history: List<Movies> = listOf(),
    val searchAnimation: ViewVisibility = ViewVisibility()
) : MvRxState
