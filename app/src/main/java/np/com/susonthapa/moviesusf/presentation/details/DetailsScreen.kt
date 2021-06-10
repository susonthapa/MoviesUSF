package np.com.susonthapa.moviesusf.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.glide.rememberGlidePainter
import np.com.susonthapa.moviesusf.domain.Movie

@Preview
@Composable
fun DetailScreenPreview() {
    val movie = Movie("221", "blade", "2019", "action", "image.png")
    DetailScreen(movie = movie)
}

@Composable
fun DetailScreen(movie: Movie) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberGlidePainter(request = movie.image),
            contentDescription = "",
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        )
        Text(
            text = movie.title,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = movie.type,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}