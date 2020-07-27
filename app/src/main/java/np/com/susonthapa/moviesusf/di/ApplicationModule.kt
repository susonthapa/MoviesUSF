package np.com.susonthapa.moviesusf.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by suson on 7/12/20
 */

@Module
class ApplicationModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }
}