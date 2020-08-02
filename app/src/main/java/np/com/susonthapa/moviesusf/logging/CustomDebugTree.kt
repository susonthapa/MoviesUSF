package np.com.susonthapa.moviesusf.logging

import timber.log.Timber

class CustomDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "(${element.fileName}:${element.lineNumber})#${element.methodName}"
    }
}

