package np.com.susonthapa.moviesusf.presentation.home

import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import np.com.susonthapa.moviesusf.utils.SingleLiveEvent
import np.com.susonthapa.moviesusf.data.Lce
import np.com.susonthapa.moviesusf.data.MoviesRepository
import np.com.susonthapa.moviesusf.data.ViewVisibility
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.domain.Movies

/**
 * Created by suson on 8/1/20
 */
class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: HomeState,
    private val repo: MoviesRepository
) : MavericksViewModel<HomeState>(initialState) {

    private val bag = CompositeDisposable()

    private val _navigateToDetails: SingleLiveEvent<Movies> = SingleLiveEvent()
    val navigateToDetails: MutableLiveData<Movies> = _navigateToDetails


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
        bag.add(
            repo.getMoviesFromServer(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
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
                }, {
                    it.printStackTrace()
                })
        )
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

    override fun onCleared() {
        super.onCleared()
        bag.clear()
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModel, HomeState> {
        override fun create(state: HomeState): HomeViewModel
    }

    companion object :
        MavericksViewModelFactory<HomeViewModel, HomeState> by daggerMavericksViewModelFactory()

}