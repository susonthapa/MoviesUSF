package np.com.susonthapa.core.ui.common

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.viewbinding.ViewBinding
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber

abstract class BaseFragment : Fragment() {

    protected var _binding: ViewBinding? = null

    protected val bag = CompositeDisposable()

    protected var uiDisposable: Disposable? = null

    protected var isRestoredFromBackStack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v(this.javaClass.simpleName)
        isRestoredFromBackStack = false
    }

    override fun onStart() {
        super.onStart()
        Timber.v(this.javaClass.simpleName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.v(this.javaClass.simpleName)
        bag.clear()
        _binding = null
        isRestoredFromBackStack = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.v(this.javaClass.simpleName)
    }

    override fun onResume() {
        super.onResume()
        Timber.v(this.javaClass.simpleName)
    }

    override fun onPause() {
        super.onPause()
        Timber.v(this.javaClass.simpleName)
        uiDisposable?.dispose()
    }

    protected fun showInputKeyboard() {
        (activity as BaseFragmentInterface).showInputKeyboard()
    }

    protected fun hideInputKeyboard() {
        (activity as BaseFragmentInterface).hideInputKeyboard(view)
    }

    protected fun showShortFeedBack(msg: String) {
        (activity as BaseFragmentInterface).showShortFeedBack(msg)
    }

    protected fun showLongFeedBack(msg: String) {
        (activity as BaseFragmentInterface).showLongFeedBack(msg)
    }

}

interface BaseFragmentInterface {

    fun hideInputKeyboard(view: View? = null)

    fun showInputKeyboard()

    fun showShortFeedBack(msg: String)

    fun showLongFeedBack(msg: String)

}
