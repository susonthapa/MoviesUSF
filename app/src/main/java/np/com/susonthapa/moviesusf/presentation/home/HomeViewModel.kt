package np.com.susonthapa.moviesusf.presentation.home

import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import np.com.susonthapa.moviesusf.data.Lce
import np.com.susonthapa.moviesusf.data.MoviesRepository
import np.com.susonthapa.moviesusf.data.ViewVisibility
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.domain.Movie
import np.com.susonthapa.moviesusf.utils.SingleLiveEvent
import timber.log.Timber

/**
 * Created by suson on 8/1/20
 */
class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: HomeState,
    private val repo: MoviesRepository
) : MavericksViewModel<HomeState>(initialState) {

    private val _navigateToDetails: SingleLiveEvent<Movie> = SingleLiveEvent()
    val navigateToDetails: MutableLiveData<Movie> = _navigateToDetails


    fun screenLoad(isRestored: Boolean) {
        withState {
            setState {
                if (isRestored) {
                    diffCopy(oldState = null)
                } else {
                    diffCopy(searchAnimation = ViewVisibility(true, isAnimated = true))
                }
            }
        }
    }

    fun searchMovie(query: String) {
        viewModelScope.launch {
            repo.getMoviesFromServer(query)
                .collect {
                    Timber.d("flow emission from repo: $it")
                    when (it) {
                        is Lce.Loading -> {
                            setState { diffCopy(searchStatus = ContentStatus.LOADING) }
                        }

                        is Lce.Content -> {
                            if (it.packet.isEmpty()) {
                                setState {
                                    diffCopy(
                                        searchResult = listOf(),
                                        searchStatus = ContentStatus.EMPTY
                                    )
                                }
                            } else {
                                setState {
                                    diffCopy(
                                        searchResult = it.packet,
                                        searchStatus = ContentStatus.LOADED
                                    )
                                }
                            }
                        }

                        is Lce.Error -> {
                            setState {
                                diffCopy(
                                    searchResult = listOf(),
                                    searchStatus = ContentStatus.error(it.throwable?.message)
                                )
                            }
                        }
                    }
                }
        }
    }

    fun addMovieToHistory(position: Int) {
        withState {
            val currentMovie = it.searchResult[position]
            val currentHistory = it.history
            val isMovieNotInHistory = currentHistory.find { it.id == currentMovie.id } == null
            if (isMovieNotInHistory) {
                val newHistory = currentHistory.toMutableList()
                newHistory.add(currentMovie)
                setState {
                    diffCopy(history = newHistory)
                }
            }

        }
    }

    fun loadMovieDetails(position: Int) {
        withState {
            val movie = it.searchResult[position]
            _navigateToDetails.postValue(movie)
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModel, HomeState> {
        override fun create(state: HomeState): HomeViewModel
    }

    companion object :
        MavericksViewModelFactory<HomeViewModel, HomeState> by daggerMavericksViewModelFactory()

}