package np.com.susonthapa.moviesusf.presentation.home

import com.google.common.truth.Truth.assertThat
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.TestObserver
import np.com.susonthapa.basetest.RxImmediateSchedulerRule
import np.com.susonthapa.moviesusf.data.Lce
import np.com.susonthapa.moviesusf.data.MoviesRepository
import np.com.susonthapa.moviesusf.domain.ContentStatus
import np.com.susonthapa.moviesusf.domain.DataStatus
import np.com.susonthapa.moviesusf.domain.Movies
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.Mockito.*
import np.com.susonthapa.moviesusf.presentation.home.HomeEvents.*
import np.com.susonthapa.moviesusf.presentation.home.HomeEffects.*


/**
 * Created by suson on 8/2/20
 */
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var stateTester: TestObserver<HomeState>
    private lateinit var effectTester: TestObserver<HomeEffects>

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
        // mock the api
        repo = mock(MoviesRepository::class.java).apply {
            `when`(getMoviesFromServer(anyString())).thenAnswer {
                if (isRequestSuccess) {
                    Observable.just(Lce.Loading(), Lce.Content(movies))
                } else {
                    Observable.just(Lce.Loading<List<Movies>>(), Lce.Error(Throwable()))
                }
            }
        }

        viewModel = HomeViewModel(repo)
        stateTester = viewModel.state.test()
        effectTester = viewModel.effect.test()

        viewModel.processEvent(ScreenLoadEvent(false))
        stateTester.assertValueCount(1)
    }

    @Test
    fun whenSearchMovie_AndMovieFound_ShowResults() {
        viewModel.processEvent(SearchMovieEvent("blade"))

        stateTester.assertValueCount(4)
        stateTester.assertValueAt(3) {
            assertThat(it.searchResult.value.size).isEqualTo(movies.size)
            true
        }
    }

    @Test
    fun whenSearchMovie_AndQueryEmpty_NoSearch() {
        viewModel.processEvent(SearchMovieEvent(""))

        stateTester.assertValueCount(1)
    }

    @Test
    fun whenSearchMovie_AndMovieEmpty_ShowEmpty() {
        movies.clear()
        viewModel.processEvent(SearchMovieEvent("blade"))

        stateTester.assertValueCount(4)
        stateTester.assertValueAt(3) {
            assertThat(it.searchStatus.value).isEqualTo(ContentStatus.EMPTY)
            assertThat(it.searchResult.value.isEmpty()).isTrue()
            true
        }
    }

    @Test
    fun whenSearchMovie_AndApiError_ShowError() {
        isRequestSuccess = false
        viewModel.processEvent(SearchMovieEvent("blade"))

        stateTester.assertValueCount(4)
        stateTester.assertValueAt(3) {
            assertThat(it.searchStatus.value.status).isEqualTo(DataStatus.ERROR)
            assertThat(it.searchResult.value.isEmpty()).isTrue()
            true
        }
    }

    @Test
    fun whenSearchMovieSuccess_AnimateSearchButton() {
        whenSearchMovie_AndMovieFound_ShowResults()

        stateTester.assertValueCount(4)
        stateTester.assertValueAt(3) {
            assertThat(it.searchAnimation.value.isAnimated).isTrue()
            true
        }
    }

    @Test
    fun whenAddToHistoryClick_UpdateHistory() {
        // first trigger some search
        whenSearchMovie_AndMovieFound_ShowResults()

        viewModel.processEvent(AddMovieToHistoryEvent(0))

        stateTester.assertValueCount(5)
        stateTester.assertValueAt(4) {
            assertThat(it.history.value.size).isEqualTo(1)
            true
        }
    }

    @Test
    fun whenAddToHistoryClick_AndMovieAlreadyAdded_NoHistoryUpdate() {
        // trigger a add to history
        whenAddToHistoryClick_UpdateHistory()

        viewModel.processEvent(AddMovieToHistoryEvent(0))

        stateTester.assertValueCount(5)
    }

    @Test
    fun whenMovieClick_LoadMovieDetails() {
        whenSearchMovie_AndMovieFound_ShowResults()
        viewModel.processEvent(LoadMovieDetailsEvent(0))

        effectTester.assertValueCount(5)
        effectTester.assertValueAt(4) {
            assertThat(it::class.java).isAssignableTo(NavigateToDetailsEffect::class.java)
            true
        }
    }



    // setup the default schedulers
    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }
}