package np.com.susonthapa.moviesusf.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import np.com.susonthapa.moviesusf.data.Lce
import np.com.susonthapa.moviesusf.data.MoviesRepository
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.domain.Movies
import javax.inject.Inject
import np.com.susonthapa.moviesusf.utils.SingleLiveEvent
import timber.log.Timber

/**
 * Created by suson on 8/1/20
 */
class HomeViewModel @Inject constructor(
    initialState: HomeState = HomeState(),
    private val repo: MoviesRepository
) : BaseMvRxViewModel<HomeState>(initialState, debugMode = true) {

    private val _navigateToMovieDetails = SingleLiveEvent<Movies>()
    val navigateToMovieDetails: LiveData<Movies> = _navigateToMovieDetails

    fun searchMovie(query: String) {
        if (query.isNotEmpty() || query.isNotBlank()) {
            viewModelScope.launch {
                repo.getMoviesFromServer(query)
                    .collect { movies ->
                        Timber.d("------ repoResponse: $movies")
                        when (movies) {
                            is Loading -> setState {
                                copy(searchStatus = ContentStatus.LOADING)
                            }

                            is Success -> {
                                setState {
                                    if (movies().isEmpty()) {
                                        copy(
                                            searchResult = movies(),
                                            searchStatus = ContentStatus.EMPTY
                                        )
                                    } else {
                                        copy(
                                            searchResult = movies(),
                                            searchStatus = ContentStatus.LOADED
                                        )
                                    }
                                }
                            }

                            is Fail -> {
                                setState {
                                    copy(
                                        searchResult = listOf(),
                                        searchStatus = ContentStatus.error(movies.error.message)
                                    )
                                }
                            }

                            is Uninitialized -> {}
                        }
                    }

            }
        }
    }

    fun addMovieToHistory(position: Int) {
        withState {
            val currentMovie = it.searchResult[position]
            val isMovieInHistory = it.history.find { movie -> movie.id == currentMovie.id } != null
            if (!isMovieInHistory) {
                val newHistory = it.history.toMutableList()
                newHistory.add(currentMovie)
                setState {
                    copy(history = newHistory)
                }
            }

        }
    }

    fun loadMovieDetails(position: Int) {
        withState {
            val movie = it.searchResult[position]
            _navigateToMovieDetails.postValue(movie)
        }
    }
}