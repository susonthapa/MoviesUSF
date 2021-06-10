package np.com.susonthapa.moviesusf.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by suson on 8/1/20
 */

@Parcelize
data class Movie(
    val id: String,
    val title: String,
    val year: String,
    val type: String,
    val image: String
) : Parcelable