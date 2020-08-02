package np.com.susonthapa.moviesusf.domain

/**
 * Created by suson on 8/2/20
 */
enum class DataStatus {
    LOADING,
    LOADED,
    EMPTY,
    ERROR
}

data class ContentStatus constructor(
    val status: DataStatus,
    val msg: String? = null) {
    companion object {
        val LOADED = ContentStatus(
            DataStatus.LOADED
        )
        val LOADING = ContentStatus(
            DataStatus.LOADING
        )
        val EMPTY = ContentStatus(
            DataStatus.EMPTY
        )
        fun error(msg: String?) =
            ContentStatus(
                DataStatus.ERROR,
                msg
            )
    }
}