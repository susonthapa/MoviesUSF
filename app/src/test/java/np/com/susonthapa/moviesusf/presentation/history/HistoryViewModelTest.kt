package np.com.susonthapa.moviesusf.presentation.history

import com.google.common.truth.Truth.assertThat
import io.reactivex.rxjava3.observers.TestObserver
import np.com.susonthapa.basetest.RxImmediateSchedulerRule
import np.com.susonthapa.moviesusf.domain.Movies
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import np.com.susonthapa.moviesusf.presentation.history.HistoryEffects.*
import np.com.susonthapa.moviesusf.presentation.history.HistoryEvents.*

/**
 * Created by suson on 8/27/20
 */
class HistoryViewModelTest {

    private lateinit var stateTester: TestObserver<HistoryState>
    private lateinit var effectTester: TestObserver<HistoryEffects>
    private lateinit var viewModel: HistoryViewModel

    private val movies = listOf(
        Movies("221", "blade", "2019", "action", "image.png"),
        Movies("222", "blade", "2019", "action", "image.png"),
        Movies("223", "blade", "2019", "action", "image.png"),
        Movies("224", "blade", "2019", "action", "image.png")
    ).toMutableList()

    @Before
    fun setup() {
        viewModel = HistoryViewModel()
        stateTester = viewModel.state.test()
        effectTester = viewModel.effect.test()

        stateTester.assertValueCount(0)
    }

    @Test
    fun whenScreenLoad_ShowInitialState() {
        viewModel.processEvent(ScreenLoadEvent(movies))

        stateTester.assertValueCount(1)
        stateTester.assertValueAt(0) {
            assertThat(it.movies.value.size).isEqualTo(movies.size)
            true
        }
    }

    @Test
    fun whenMovieRemove_RemoveFromList() {
        whenScreenLoad_ShowInitialState()
        viewModel.processEvent(RemoveMovieEvent(0))

        stateTester.assertValueCount(2)
        stateTester.assertValueAt(1) {
            assertThat(it.movies.value.size).isEqualTo(movies.size - 1)
            true
        }
    }

    @Test
    fun whenMovieRemoved_AndMovieEmpty_NavigateBack() {
        whenScreenLoad_ShowInitialState()
        for (i in 0 until movies.size) {
            viewModel.processEvent(RemoveMovieEvent(0))
        }

        stateTester.assertValueCount(movies.size)
        effectTester.assertValueCount(movies.size + 1)
        effectTester.assertValueAt(movies.size) {
            assertThat(it::class.java).isAssignableTo(NavigateBackEffect::class.java)
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