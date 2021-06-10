package np.com.susonthapa.moviesusf.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.glide.rememberGlidePainter
import np.com.susonthapa.moviesusf.R
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.domain.DataStatus
import np.com.susonthapa.moviesusf.domain.Movie

@Composable
fun ComposeHomeScreen(navigateToDetails: (Movie) -> Unit) {
    val viewModel: HomeViewModel = mavericksViewModel()

    val searchList by viewModel.collectAsState(HomeState::searchResult)
    val searchStatus by viewModel.collectAsState(HomeState::searchStatus)
    val historyList by viewModel.collectAsState(HomeState::history)

    viewModel.navigateToDetails.observe(o)

    Surface(modifier = Modifier.fillMaxSize()) {
        val searchState = remember { mutableStateOf("") }
        Column(modifier = Modifier.fillMaxHeight()) {
            SearchLayout(query = searchState.value, onQueryChange = { searchState.value = it }) {
                viewModel.searchMovie(searchState.value)
            }

            if (searchStatus.status == DataStatus.LOADING) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            SearchListWithStatus(
                searchList = searchList,
                searchStatus = searchStatus,
                modifier = Modifier.weight(1f),
                navigateToDetails = navigateToDetails
            ) { viewModel.addMovieToHistory(it) }
            HistoryList(movies = historyList)
        }
    }
}

@Composable
private fun SearchListWithStatus(
    searchList: List<Movie>,
    searchStatus: ContentStatus,
    modifier: Modifier,
    navigateToDetails: (Movie) -> Unit = {},
    addToHistory: (Int) -> Unit = {}
) {
    when (searchStatus.status) {
        DataStatus.LOADED -> {
            SearchResultListLayout(
                movies = searchList,
                modifier = modifier,
                addToHistory = addToHistory,
                navigateToDetails
            )
        }

        DataStatus.EMPTY -> {
            EmptyView(
                modifier = modifier
                    .padding(16.dp)
            )
        }

        DataStatus.ERROR -> {
            ErrorView(
                modifier = modifier
                    .padding(16.dp)
            )
        }

        else -> {

        }
    }
}


@Composable
private fun SearchLayout(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(value = query, onValueChange = onQueryChange, modifier = Modifier.weight(1f))
        Button(
            onClick = onSearchClick, shape = CircleShape,
            modifier = Modifier
                .height(48.dp)
                .width(64.dp)
                .padding(start = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_search_24),
                contentDescription = "", modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
            )
        }
    }
}

@Composable
fun ErrorView(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = "",
            modifier = Modifier
                .height(100.dp)
                .width(50.dp)
        )

        Text(
            text = "Something went wrong!",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 10.dp)
        )

        Button(
            onClick = {},
            shape = MaterialTheme.shapes.small.copy(CornerSize(18.dp)),
            modifier = Modifier
                .padding(top = 8.dp)
                .width(100.dp)
        ) {
            Text(text = "RETRY")
        }
    }
}

@Composable
fun EmptyView(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty),
            contentDescription = "",
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
        )

        Text(
            text = "Nothing here!",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun SearchResultListLayout(
    movies: List<Movie>,
    modifier: Modifier,
    addToHistory: (Int) -> Unit,
    onItemClick: (Movie) -> Unit
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(items = movies) { index, movie ->
            SearchResultListItem(
                movie = movie,
                addToHistory = { addToHistory(index) },
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
private fun SearchResultListItem(
    movie: Movie,
    addToHistory: () -> Unit,
    onItemClick: (Movie) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick(movie) }
    ) {
        Image(
            painter = rememberGlidePainter(
                request = movie.image,
                previewPlaceholder = R.drawable.ic_empty
            ),
            contentDescription = "",
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Text(text = movie.title, style = MaterialTheme.typography.h5)
            Text(
                text = movie.type,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = movie.year,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_add_circle),
            contentDescription = "",
            modifier = Modifier
                .clickable { addToHistory() }
                .align(Alignment.CenterVertically)

        )
    }
}

@Composable
private fun HistoryList(movies: List<Movie>) {
    LazyRow {
        items(items = movies) { movie ->
            HistoryListItem(movie = movie)
        }
    }
}

@Composable
private fun HistoryListItem(movie: Movie) {
    Image(
        painter = rememberGlidePainter(
            request = movie.image,
            previewPlaceholder = R.drawable.ic_empty,
        ),
        contentDescription = "",
        modifier = Modifier
            .height(100.dp)
            .width(100.dp)
            .padding(8.dp)
    )
}

@Preview
@Composable
fun SearchResultLayoutPreview() {
    val movies = listOf(
        Movie("221", "blade", "2019", "action", "image.png"),
        Movie("222", "blade", "2019", "action", "image.png"),
        Movie("223", "blade", "2019", "action", "image.png"),
        Movie("224", "blade", "2019", "action", "image.png")
    ).toMutableList()
    SearchResultListLayout(movies = movies, modifier = Modifier, {}) {}
}

@Preview
@Composable
fun SearchResultItemPreview() {
    SearchResultListItem(movie = Movie("221", "blade", "2019", "action", "image.png"), {}) {}
}

@Preview
@Composable
fun SearchStatusEmptyPreview() {
    SearchListWithStatus(
        searchList = listOf(),
        searchStatus = ContentStatus.EMPTY,
        modifier = Modifier.fillMaxSize()
    )
}

@Preview
@Composable
fun SearchStatusErrorPreview() {
    SearchListWithStatus(
        searchList = listOf(),
        searchStatus = ContentStatus.error(null),
        modifier = Modifier.fillMaxSize()
    )
}