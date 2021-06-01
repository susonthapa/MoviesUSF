package np.com.susonthapa.moviesusf.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import np.com.susonthapa.moviesusf.domain.Movies
import javax.inject.Inject

/**
 * Created by suson on 7/13/20
 */

class MoviesRepository @Inject constructor(
    private val api: ApiService
) {

    fun getMoviesFromServer(query: String): Flow<Lce<List<Movies>>> {
        return flow<Lce<List<Movies>>> {
            val response = api.getMovies(query)
            if (response.response == "True") {
                val movies = convertSearchResponse(response.search)
                emit(Lce.Content(movies))
            } else {
                Lce.Error<List<Movies>>(Throwable(response.error))
            }
        }.catch { e ->
            e.printStackTrace()
            emit(Lce.Error(e))
        }.onStart { emit(Lce.Loading()) }
            .flowOn(Dispatchers.IO)
    }

    private fun convertSearchResponse(response: List<SearchResponse.Search>): List<Movies> {
        return response.map {
            Movies(it.id, it.title, it.year, it.type, it.image)
        }
    }

}