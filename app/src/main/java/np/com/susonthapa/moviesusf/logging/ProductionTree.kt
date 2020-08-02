package np.com.susonthapa.moviesusf.logging

import android.util.Log.ERROR
import android.util.Log.WARN
import timber.log.Timber

class ProductionTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == ERROR || priority == WARN) {
            // TODO(firebase crashlytics)
        }
    }
}