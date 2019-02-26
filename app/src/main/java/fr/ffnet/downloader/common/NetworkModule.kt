package fr.ffnet.downloader.common

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.BuildConfig
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkhttpClient(
        context: Context,
        dispatcher: Dispatcher
    ): OkHttpClient.Builder = OkHttpClient().newBuilder()
            .addInterceptor(ChuckInterceptor(context))
            .addNetworkInterceptor(StethoInterceptor())
            .dispatcher(dispatcher)

    @Provides
    fun provideDispatcher(): Dispatcher {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 3
        return dispatcher
    }

    @Provides
    fun provideRetrofitMoschi(
        moshiConverterFactory: MoshiConverterFactory,
        client: OkHttpClient.Builder
    ): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(client.build())
                .addConverterFactory(moshiConverterFactory)
                .build()
    }

    @Provides
    fun provideMoshiConverter(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()
}
