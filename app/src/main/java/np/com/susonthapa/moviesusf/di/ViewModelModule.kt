package np.com.susonthapa.moviesusf.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import np.com.susonthapa.moviesusf.data.MoviesRepository
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
    fun bindHomeViewModel(repo: MoviesRepository): ViewModel {
        return HomeViewModel(repo = repo)
    }
}