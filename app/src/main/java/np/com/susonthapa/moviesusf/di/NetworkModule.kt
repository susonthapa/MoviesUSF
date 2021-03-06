package np.com.susonthapa.moviesusf.di

import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.schedulers.Schedulers
import np.com.susonthapa.moviesusf.data.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Created by suson on 7/12/20
 */

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
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

        return retrofit.create(ApiService::class.java)
    }
}
