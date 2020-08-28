package np.com.susonthapa.moviesusf.presentation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import np.com.susonthapa.core.ui.common.BaseFragmentInterface
import np.com.susonthapa.moviesusf.R
import np.com.susonthapa.moviesusf.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BaseFragmentInterface {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun hideInputKeyboard(view: View?) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken ?: currentFocus?.windowToken, 0)
    }

    override fun showInputKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun showShortFeedBack(msg: String) {
        val snackBar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
        snackBar.show()
    }

    override fun showLongFeedBack(msg: String) {
        val snackBar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
        snackBar.show()
    }

}