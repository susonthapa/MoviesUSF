package np.com.susonthapa.moviesusf.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import np.com.susonthapa.moviesusf.data.MoviesRepository
import np.com.susonthapa.moviesusf.di.ViewModelFactory
import np.com.susonthapa.moviesusf.di.ViewModelKey
import np.com.susonthapa.moviesusf.presentation.history.HistoryViewModel
import np.com.susonthapa.moviesusf.presentation.home.HomeViewModel
import javax.inject.Provider

/**
 * Created by suson on 7/13/20
 */

@Module
class ViewModelModule {
    @Provides
    fun provideViewModelFactory(providerMap: MutableMap<Class<out ViewModel>, Provider<ViewModel>>): ViewModelFactory {
        return ViewModelFactory(providerMap)
    }

    @Provides
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun provideHomeViewModel(repo: MoviesRepository): ViewModel {
        return HomeViewModel(repo)
    }

    @Provides
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    fun provideHistoryViewModel(): ViewModel {
        return HistoryViewModel()
    }
}