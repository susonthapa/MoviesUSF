package np.com.susonthapa.moviesusf.data

import np.com.susonthapa.moviesusf.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by suson on 7/27/20
 */

interface ApiService {

    @GET("/")
    suspend fun getMovies(
        @Query("s") name: String,
        @Query("apiKey") apiKey: String = BuildConfig.OMDB_API_KEY
    ): SearchResponse

}