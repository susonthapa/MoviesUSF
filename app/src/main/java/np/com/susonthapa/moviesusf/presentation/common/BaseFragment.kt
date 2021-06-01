package np.com.susonthapa.core.ui.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import timber.log.Timber

abstract class BaseFragment : Fragment() {

    protected var _binding: ViewBinding? = null

    protected var isRestoredFromBackStack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v("Fragment onCreate ${this.javaClass.simpleName}")
        isRestoredFromBackStack = false
    }

    override fun onStart() {
        Timber.v("Fragment onStart ${this.javaClass.simpleName}")
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.v("Fragment onDestroyView ${this.javaClass.simpleName}")
        _binding = null
        isRestoredFromBackStack = true
    }

    protected fun hideInputKeyboard() {
        (activity as BaseFragmentInterface).hideInputKeyboard()
    }

    protected fun showShortFeedBack(msg: String) {
        (activity as BaseFragmentInterface).showShortFeedBack(msg)
    }

    protected fun showLongFeedBack(msg: String) {
        (activity as BaseFragmentInterface).showLongFeedBack(msg)
    }

    protected fun showBottomNavigation() {
        (activity as BaseFragmentInterface).showBottomNavigation()
    }

    protected fun hideBottomNavigation() {
        (activity as BaseFragmentInterface).hideBottomNavigation()
    }
}

interface BaseFragmentInterface {

    fun hideInputKeyboard()

    fun showShortFeedBack(msg: String)

    fun showLongFeedBack(msg: String)

    fun showBottomNavigation()

    fun hideBottomNavigation()

}
