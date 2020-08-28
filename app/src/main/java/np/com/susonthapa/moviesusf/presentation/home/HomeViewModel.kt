package np.com.susonthapa.moviesusf.presentation.home

import android.view.View
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import np.com.susonthapa.moviesusf.data.Lce
import np.com.susonthapa.moviesusf.data.MoviesRepository
import np.com.susonthapa.moviesusf.data.ViewBox
import np.com.susonthapa.moviesusf.data.ViewVisibility
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.presentation.usf.UViewModel
import javax.inject.Inject
import np.com.susonthapa.moviesusf.presentation.home.HomeEffects.*
import np.com.susonthapa.moviesusf.presentation.home.HomeEvents.*
import np.com.susonthapa.moviesusf.presentation.home.HomeResults.*
import np.com.susonthapa.moviesusf.utils.withLatestFrom
import np.com.susonthapa.moviesusf.utils.withLatestStateBox
import java.util.concurrent.TimeUnit

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
                    o.ofType(ScreenLoadEvent::class.java).map { ScreenLoadResult(it.isRestored) },
                    o.ofType(SearchMovieEvent::class.java).compose(searchMovie()),
                    o.ofType(MovieTypingEvent::class.java).compose(movieTyping()),
                    o.ofType(AddMovieToHistoryEvent::class.java).compose(addMovieToHistory()),
                    o.ofType(LoadMovieDetailsEvent::class.java).compose(loadMovieDetails()),
                    o.ofType(ViewHistoryEvent::class.java).compose(viewHistory())
                )
            )
        }
    }

    override fun resultToState(results: Observable<out HomeResults>): Observable<HomeState> {
        return results
            .scan(HomeState()) { vs, result ->
                when (result) {
                    is ScreenLoadResult -> {
                        if (result.isRestored) {
                            // change state when the fragment is restored
                            vs.resetCopy()
                        } else {
                            vs.resetCopy()
                        }
                    }

                    is SearchMovieResult -> {
                        vs.stateCopy(
                            searchResult = ViewBox(result.movies), searchAnimation = ViewBox(
                                ViewVisibility(true, isAnimated = true)
                            )
                        )

                    }

                    is LoadSuggestionResult -> {
                        vs.stateCopy(
                            searchSuggestions = ViewBox(result.suggestions)
                        )
                    }

                    is SuggestionStatusResult -> {
                        vs.stateCopy(suggestionStatus = ViewBox(result.status))
                    }

                    is SearchMovieStatusResult -> {
                        vs.stateCopy(
                            searchStatus = ViewBox(result.status)
                        )
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

                is ViewHistoryResult -> {
                    NavigateToHistoryEffect(result.movies)
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
                        .flatMap {
                            when (val response = it) {
                                is Lce.Loading -> {
                                    Observable.just(
                                        SearchMovieStatusResult(ContentStatus.LOADING)
                                    )
                                }

                                is Lce.Content -> {
                                    if (response.packet.isEmpty()) {
                                        Observable.just(
                                            SearchMovieStatusResult(ContentStatus.EMPTY),
                                            SearchMovieResult(
                                                listOf()
                                            )
                                        )
                                    } else {
                                        Observable.just(
                                            SearchMovieStatusResult(ContentStatus.LOADED),
                                            SearchMovieResult(response.packet)
                                        )
                                    }
                                }

                                is Lce.Error -> {
                                    Observable.just(
                                        SearchMovieStatusResult(
                                            ContentStatus.error(
                                                response.throwable?.message
                                            )
                                        ),
                                        SearchMovieResult(listOf())
                                    )
                                }
                            }
                        }
                        .startWith(Observable.just(LoadSuggestionResult(listOf())))
                }
        }
    }

    /**
     * We use debounce operator for throttling the user input along with switchMap to cancel any
     * previous pending search result if new query is available
     */
    private fun movieTyping(): ObservableTransformer<MovieTypingEvent, out HomeResults> {
        return ObservableTransformer { observable ->
            observable
                .filter {
                    it.query.isNotEmpty() || it.query.isNotBlank()
                }
                .debounce(500, TimeUnit.MILLISECONDS)
                .switchMap { e ->
                    repo.getMoviesFromServer(e.query)
                        .flatMap {
                            when (val response = it) {
                                is Lce.Loading -> {
                                    Observable.just(
                                        SuggestionStatusResult(ContentStatus.LOADING)
                                    )
                                }

                                is Lce.Content -> {
                                    if (response.packet.isEmpty()) {
                                        Observable.just(
                                            SuggestionStatusResult(ContentStatus.EMPTY),
                                            LoadSuggestionResult(
                                                listOf()
                                            )
                                        )
                                    } else {
                                        Observable.just(
                                            SuggestionStatusResult(ContentStatus.LOADED),
                                            LoadSuggestionResult(response.packet.map { it.title })
                                        )
                                    }
                                }

                                is Lce.Error -> {
                                    Observable.just(
                                        SuggestionStatusResult(
                                            ContentStatus.error(
                                                response.throwable?.message
                                            )
                                        ),
                                        LoadSuggestionResult(listOf())
                                    )
                                }
                            }
                        }
                }
        }
    }


    private fun addMovieToHistory(): ObservableTransformer<AddMovieToHistoryEvent, out HomeResults> {
        return ObservableTransformer { observable ->
            observable
                .withLatestStateBox(mState)
                .map { s ->
                    val currentMovie = s.state.searchResult?.value[s.event.position]
                    val currentHistory = s.state.history.value
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
                .withLatestStateBox(mState)
                .map { s ->
                    val movie = s.state.searchResult.value[s.event.position]
                    LoadMovieDetailsResult(movie)
                }
        }
    }

    private fun viewHistory(): ObservableTransformer<ViewHistoryEvent, ViewHistoryResult> {
        return ObservableTransformer { upstream ->
            upstream.withLatestStateBox(mState)
                .map { s ->
                    val history = s.state.history.value
                    ViewHistoryResult(history)
                }
        }
    }

}