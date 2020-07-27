package np.com.susonthapa.core.ui.common

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber

abstract class DisposingViewModel : ViewModel() {
    private val bag = CompositeDisposable()

    protected fun addDisposable(disposable: Disposable) {
        bag.add(disposable)
    }

    protected fun clearSubscriptions() {
        bag.clear()
    }

    override fun onCleared() {
        Timber.d("Clearing disposables")
        bag.clear()
    }

}