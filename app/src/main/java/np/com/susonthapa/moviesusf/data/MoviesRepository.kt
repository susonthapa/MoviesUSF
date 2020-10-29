package np.com.susonthapa.moviesusf.data

import io.reactivex.rxjava3.core.Observable
import np.com.susonthapa.moviesusf.domain.Movies
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by suson on 7/13/20
 */

class MoviesRepository  constructor(
    private val api: ApiService
) {

    fun getMoviesFromServer(query: String): Observable<Lce<List<Movies>>> {
        return api.getMovies(query)
            .map<Lce<List<Movies>>> {
                if (it.response == "True") {
                    val movies = convertSearchResponse(it.search)
                    Lce.Content(movies)
                } else {
                    Lce.Error(Throwable(it.error))
                }
            }
            .onErrorReturn {
                it.printStackTrace()
                Lce.Error(it)
            }
            .startWith(Observable.just(Lce.Loading()))
    }

    private fun convertSearchResponse(response: List<SearchResponse.Search>): List<Movies> {
        return response.map {
            Movies(it.id, it.title, it.year, it.type, it.image)
        }
    }

}