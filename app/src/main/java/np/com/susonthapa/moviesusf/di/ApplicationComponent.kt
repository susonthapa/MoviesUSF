package np.com.susonthapa.moviesusf.di

import com.airbnb.mvrx.MavericksViewModel
import dagger.Component
import np.com.susonthapa.moviesusf.presentation.home.AssistedViewModelFactory
import np.com.susonthapa.moviesusf.presentation.MainActivity
import np.com.susonthapa.moviesusf.presentation.details.DetailsFragment
import np.com.susonthapa.moviesusf.presentation.home.HomeFragment
import javax.inject.Singleton

/**
 * Created by suson on 7/12/20
 */

@Singleton
@Component(modules = [NetworkModule::class, ViewModelModule::class, ApplicationModule::class, HomeModule::class])
interface ApplicationComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: DetailsFragment)

    fun viewModelFactories(): Map<Class<out MavericksViewModel<*>>, AssistedViewModelFactory<*, *>>
}
