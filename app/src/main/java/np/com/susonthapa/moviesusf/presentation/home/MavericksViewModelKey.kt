package np.com.susonthapa.moviesusf.presentation.home

import com.airbnb.mvrx.MavericksViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Created by suson on 4/30/21
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class MavericksViewModelKey(val value: KClass<out MavericksViewModel<*>>)