package np.com.susonthapa.moviesusf.presentation.navigator

sealed class Screen(val route: String) {
    object Home: Screen("home")
    object Detail: Screen("detail")
}