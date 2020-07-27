package np.com.susonthapa.ssotmovies.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by suson on 7/13/20
 */

@JsonClass(generateAdapter = true)
data class SearchResponse(
    @Json(name = "Response")
    val response: String,
    @Json(name = "Search")
    val search: List<Search>,
    @Json(name = "Error")
    val error: String?
) {

    @JsonClass(generateAdapter = true)
    data class Search(
        @Json(name = "imdbID")
        val id: String,
        @Json(name = "Title")
        val title: String,
        @Json(name = "Year")
        val year: String,
        @Json(name = "Type")
        val type: String,
        @Json(name = "Poster")
        val image: String
    )
}