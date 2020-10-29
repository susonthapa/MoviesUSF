package np.com.susonthapa.moviesusf.di

import io.reactivex.rxjava3.schedulers.Schedulers
import np.com.susonthapa.moviesusf.data.ApiService
import np.com.susonthapa.moviesusf.data.MoviesRepository
import np.com.susonthapa.moviesusf.presentation.home.HomeViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by suson on 10/29/20
 */

val networkModule = module {

    single {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(client)
            .baseUrl("https://www.omdbapi.com")
            .build()

        retrofit.create(ApiService::class.java)
    }

}

val viewModelModule = module {
    viewModel {
        HomeViewModel(get())
    }
}

val repoModule = module {
    single {
        MoviesRepository(get())
    }
}