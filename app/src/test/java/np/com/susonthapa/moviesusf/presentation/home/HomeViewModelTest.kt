package np.com.susonthapa.moviesusf.presentation.home

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import np.com.susonthapa.moviesusf.data.MoviesRepository
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.domain.DataStatus
import np.com.susonthapa.moviesusf.domain.Movies
import org.junit.After
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.Mockito.*


/**
 * Created by suson on 8/2/20
 */
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class HomeViewModelTest {
    @ObsoleteCoroutinesApi
    private val mainThread = newSingleThreadContext("Main Thread")
    private lateinit var viewModel: HomeViewModel

    private lateinit var repo: MoviesRepository

    private var isRequestSuccess = true

    private val movies = listOf(
        Movies("221", "blade", "2019", "action", "image.png"),
        Movies("222", "blade", "2019", "action", "image.png"),
        Movies("223", "blade", "2019", "action", "image.png"),
        Movies("224", "blade", "2019", "action", "image.png")
    ).toMutableList()

    @Before
    fun setup() {
        Dispatchers.setMain(mainThread)
        // mock the api
        repo = mock(MoviesRepository::class.java).apply {
            runBlockingTest {
                `when`(getMoviesFromServer(anyString())).thenAnswer {
                    if (isRequestSuccess) {
                        flowOf(Loading(), Success(movies))
                    } else {
                        flowOf(Loading(), Fail(Throwable()))
                    }
                }
            }
        }

        viewModel = HomeViewModel(repo = repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThread.close()
    }

    @Test
    fun whenSearchMovie_AndMovieFound_ShowResults() {
        runBlockingTest {
            viewModel.searchMovie("blade")
            withState(viewModel) {
                assertThat(it.searchResult.size).isEqualTo(movies.size)
            }
        }
    }

    @Test
    fun whenSearchMovie_AndQueryEmpty_NoSearch() {
        viewModel.searchMovie("")
        withState(viewModel) {
            assertThat(it.searchResult.isEmpty()).isTrue()
        }
    }

    @Test
    fun whenSearchMovie_AndMovieEmpty_ShowEmpty() {
        movies.clear()
        viewModel.searchMovie("blade")
        withState(viewModel) {
            assertThat(it.searchStatus).isEqualTo(ContentStatus.EMPTY)
            assertThat(it.searchResult.isEmpty()).isTrue()
        }
    }

    @Test
    fun whenSearchMovie_AndApiError_ShowError() {
        isRequestSuccess = false
        viewModel.searchMovie("blade")
        withState(viewModel) {
            assertThat(it.searchStatus.status).isEqualTo(DataStatus.ERROR)
            assertThat(it.searchResult.isEmpty()).isTrue()
        }
    }

    @Test
    fun whenSearchMovieSuccess_AnimateSearchButton() {
        whenSearchMovie_AndMovieFound_ShowResults()
        withState(viewModel) {
            assertThat(it.searchAnimation.isAnimated).isTrue()
        }
    }

    @Test
    fun whenAddToHistoryClick_UpdateHistory() {
        // first trigger some search
        whenSearchMovie_AndMovieFound_ShowResults()
        viewModel.addMovieToHistory(0)
        withState(viewModel) {
            assertThat(it.history.size).isEqualTo(1)
        }
    }

    @Test
    fun whenAddToHistoryClick_AndMovieAlreadyAdded_NoHistoryUpdate() {
        // trigger a add to history
        whenAddToHistoryClick_UpdateHistory()
        viewModel.addMovieToHistory(0)
        withState(viewModel) {
            assertThat(it.history.size).isEqualTo(1)
        }
    }
    companion object {
        @JvmField
        @ClassRule
        val mvrxTestRule = MvRxTestRule()
    }
}