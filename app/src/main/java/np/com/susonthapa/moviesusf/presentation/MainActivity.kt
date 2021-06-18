package np.com.susonthapa.moviesusf.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import np.com.susonthapa.moviesusf.R
import np.com.susonthapa.moviesusf.domain.Movie
import np.com.susonthapa.moviesusf.presentation.details.DetailScreen
import np.com.susonthapa.moviesusf.presentation.home.ComposeHomeScreen
import np.com.susonthapa.moviesusf.presentation.navigator.Screen
import np.com.susonthapa.moviesusf.presentation.theme.MoviesUSFTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoviesUSFTheme {
                MoviesUSFApp()
            }
        }
    }
}

@Composable
fun MoviesUSFApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            ComposeHomeScreen {
                navController.currentBackStackEntry?.arguments = Bundle().apply {
                    putParcelable("movie", it)
                }
                navController.navigate(Screen.Detail.route)
            }
        }

        composable(route = Screen.Detail.route) {
            val movie = navController.previousBackStackEntry?.arguments?.getParcelable<Movie>("movie")
            DetailScreen(movie = movie!!)
        }
    }

}