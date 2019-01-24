package com.citymapper.codingchallenge.common

import android.content.Context
import com.citymapper.codingchallenge.BuildConfig
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkhttpClient(
        context: Context
    ): OkHttpClient.Builder = OkHttpClient().newBuilder()
        .addInterceptor(ChuckInterceptor(context))
        .addInterceptor(AuthInterceptor(BuildConfig.APP_ID, BuildConfig.APP_KEY))

    @Provides
    fun provideRetrofitMoschi(
        moshiConverterFactory: MoshiConverterFactory,
        client: OkHttpClient.Builder
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
