package np.com.susonthapa.moviesusf.presentation.home

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.functions.BiFunction
import np.com.susonthapa.core.ui.common.DisposingViewModel
import np.com.susonthapa.moviesusf.data.Lce
import np.com.susonthapa.moviesusf.data.MoviesRepository
import np.com.susonthapa.moviesusf.data.ViewBox
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.domain.Movies
import np.com.susonthapa.moviesusf.presentation.usf.UViewModel
import javax.inject.Inject
import np.com.susonthapa.moviesusf.presentation.home.HomeEffects.*
import np.com.susonthapa.moviesusf.presentation.home.HomeEvents.*
import np.com.susonthapa.moviesusf.presentation.home.HomeResults.*
import np.com.susonthapa.moviesusf.utils.withLatestFrom

/**
 * Created by suson on 8/1/20
 */
class HomeViewModel @Inject constructor(
    private val repo: MoviesRepository
) : UViewModel<HomeEvents, HomeResults, HomeState, HomeEffects>() {

    override fun eventToResult(events: Observable<HomeEvents>): Observable<out HomeResults> {
        return events.publish { o ->
            Observable.merge(
                arrayListOf(
                    o.ofType(ScreenLoadEvent::class.java).map { ScreenLoadResult },
                    o.ofType(SearchMovieEvent::class.java).compose(searchMovie()),
                    o.ofType(AddMovieToHistoryEvent::class.java).compose(addMovieToHistory()),
                    o.ofType(LoadMovieDetailsEvent::class.java).compose(loadMovieDetails())
                )
            )
        }
    }

    override fun resultToState(results: Observable<out HomeResults>): Observable<HomeState> {
        return results
            .scan(HomeState()) { vs, result ->
                when (result) {
                    is ScreenLoadResult -> {
                        vs.resetCopy()
                    }

                    is SearchMovieResult -> {
                        vs.stateCopy(searchResult = ViewBox(result.movies))
                    }

                    is SearchMovieStatusResult -> {
                        vs.stateCopy(searchStatus = ViewBox(result.status))
                    }

                    is AddMovieToHistoryResult -> {
                        vs.stateCopy(history = ViewBox(result.history))
                    }

                    else -> {
                        vs
                    }
                }

            }
            .distinctUntilChanged()
    }

    override fun resultToEffect(results: Observable<out HomeResults>): Observable<HomeEffects> {
        return results.map { result ->
            when (result) {
                is LoadMovieDetailsResult -> {
                    NavigateToDetailsEffect(result.movie)
                }

                else -> {
                    NoEffect
                }
            }
        }
    }

    private fun searchMovie(): ObservableTransformer<SearchMovieEvent, out HomeResults> {
        return ObservableTransformer { observable ->
            observable
                .filter {
                    it.query.isNotEmpty() || it.query.isNotBlank()
                }
                .switchMap { e ->
                    repo.getMoviesFromServer(e.query)
                        .withLatestFrom(mState) { response, state ->
                            Pair(response, state)
                        }
                        .flatMap { combinedResult ->
                            val it = combinedResult.first
                            when (it) {
                                is Lce.Loading -> {
                                    Observable.just(SearchMovieStatusResult(ContentStatus.LOADING))
                                }

                                is Lce.Content -> {
                                    if (it.packet.isEmpty()) {
                                        Observable.just(SearchMovieStatusResult(ContentStatus.EMPTY))
                                    } else {
                                        Observable.just(
                                            SearchMovieStatusResult(ContentStatus.LOADED),
                                            SearchMovieResult(it.packet)
                                        )
                                    }
                                }

                                is Lce.Error -> {
                                    Observable.just(SearchMovieStatusResult(ContentStatus.error(it.throwable?.message)))
                                }
                            }
                        }
                }
        }
    }

    private fun addMovieToHistory(): ObservableTransformer<AddMovieToHistoryEvent, out HomeResults> {
        return ObservableTransformer { observable ->
            observable
                .withLatestFrom(mState) { event, state ->
                    val currentMovie = state.searchResult?.value[event.position]
                    val currentHistory = state.history.value
                    val isMovieInHistory = currentHistory.find {
                        it.id == currentMovie.id
                    } != null
                    if (isMovieInHistory) {
                        NoResult
                    } else {
                        val newHistory = currentHistory.toMutableList()
                        newHistory.add(currentMovie)
                        AddMovieToHistoryResult(newHistory)
                    }
                }
        }
    }

    private fun loadMovieDetails(): ObservableTransformer<LoadMovieDetailsEvent, LoadMovieDetailsResult> {
        return ObservableTransformer { observable ->
            observable
                .withLatestFrom(mState) {event, state ->
                    val movie = state.searchResult.value[event.position]
                    LoadMovieDetailsResult(movie)
                }
        }
    }

}