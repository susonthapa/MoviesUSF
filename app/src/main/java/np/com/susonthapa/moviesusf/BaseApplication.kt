package np.com.susonthapa.moviesusf

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import np.com.susonthapa.moviesusf.di.*
import np.com.susonthapa.moviesusf.logging.CustomDebugTree
import np.com.susonthapa.moviesusf.logging.ProductionTree
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

/**
 * Created by suson on 7/13/20
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            modules(networkModule, viewModelModule, repoModule)
        }
        Fresco.initialize(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(CustomDebugTree())
        } else {
            Timber.plant(ProductionTree())
        }
    }

}