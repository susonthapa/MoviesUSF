package np.com.susonthapa.moviesusf.data

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import np.com.susonthapa.moviesusf.domain.Movies
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by suson on 7/13/20
 */

class MoviesRepository @Inject constructor(
    private val api: ApiService
) {

    fun getMoviesFromServer(query: String): Flow<Async<List<Movies>>> {
        return flow {
            // we are using this builder as flowOf() will suspend and wouldn't immediately return the
            // loading
            emit(api.getMovies(query))
        }.map {
            if (it.response == "True") {
                val movies = convertSearchResponse(it.search)
                Success(movies)
            } else {
                Fail(Throwable(it.error))
            }
        }.catch { e ->
            e.printStackTrace()
            emit(Fail(e))
        }.onStart { emit(Loading()) }
            .flowOn(Dispatchers.IO)
    }

    private fun convertSearchResponse(response: List<SearchResponse.Search>): List<Movies> {
        return response.map {
            Movies(it.id, it.title, it.year, it.type, it.image)
        }
    }

}