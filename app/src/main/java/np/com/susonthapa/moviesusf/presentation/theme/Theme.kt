package np.com.susonthapa.moviesusf.presentation.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

val LightThemeColors = lightColors(
    primary = Color_6200ee,
    primaryVariant = Color_3700b3,
    secondary = Color_03dac5
)




@Composable
fun MoviesUSFTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = LightThemeColors,
        typography = Typography,
        content = content
    )
}