package np.com.susonthapa.moviesusf.data

import io.reactivex.rxjava3.core.Observable
import np.com.susonthapa.moviesusf.BuildConfig
import np.com.susonthapa.ssotmovies.network.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by suson on 7/27/20
 */

interface ApiService {

    @GET("/")
    fun getMovies(
        @Query("s") name: String = "blade",
        @Query("apiKey") apiKey: String = BuildConfig.OMDB_API_KEY
    ): Observable<SearchResponse>

}