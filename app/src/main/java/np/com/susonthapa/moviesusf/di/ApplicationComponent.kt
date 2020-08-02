package np.com.susonthapa.moviesusf.di

import dagger.Component
import np.com.susonthapa.moviesusf.presentation.MainActivity
import np.com.susonthapa.moviesusf.presentation.details.DetailsFragment
import np.com.susonthapa.moviesusf.presentation.home.HomeFragment
import javax.inject.Singleton

/**
 * Created by suson on 7/12/20
 */

@Singleton
@Component(modules = [NetworkModule::class, ViewModelModule::class, ApplicationModule::class])
interface ApplicationComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: DetailsFragment)
}
