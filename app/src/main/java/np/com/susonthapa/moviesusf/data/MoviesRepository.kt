package np.com.susonthapa.moviesusf.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import np.com.susonthapa.moviesusf.domain.Movie
import javax.inject.Inject

/**
 * Created by suson on 7/13/20
 */

class MoviesRepository @Inject constructor(
    private val api: ApiService
) {

    fun getMoviesFromServer(query: String): Flow<Lce<List<Movie>>> {
        return flow<Lce<List<Movie>>> {
            val response = api.getMovies(query)
            if (response.response == "True") {
                val movies = convertSearchResponse(response.search)
                emit(Lce.Content(movies))
            } else {
                Lce.Error<List<Movie>>(Throwable(response.error))
            }
        }.catch { e ->
            e.printStackTrace()
            emit(Lce.Error(e))
        }.onStart { emit(Lce.Loading()) }
            .flowOn(Dispatchers.IO)
    }

    private fun convertSearchResponse(response: List<SearchResponse.Search>): List<Movie> {
        return response.map {
            Movie(it.id, it.title, it.year, it.type, it.image)
        }
    }

}