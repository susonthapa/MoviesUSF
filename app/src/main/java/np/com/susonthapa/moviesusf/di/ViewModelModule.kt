package np.com.susonthapa.moviesusf.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import np.com.susonthapa.moviesusf.data.MoviesRepository
import np.com.susonthapa.moviesusf.di.ViewModelFactory
import np.com.susonthapa.moviesusf.di.ViewModelKey
import np.com.susonthapa.moviesusf.presentation.home.AssistedViewModelFactory
import np.com.susonthapa.moviesusf.presentation.home.HomeViewModel
import np.com.susonthapa.moviesusf.presentation.home.MavericksViewModelKey
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

}

@Module
interface HomeModule {
    @Binds
    @IntoMap
    @MavericksViewModelKey(HomeViewModel::class)
    fun bindHomeViewModel(factory: HomeViewModel.Factory): AssistedViewModelFactory<*, *>
}