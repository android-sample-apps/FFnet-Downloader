package com.citymapper.codingchallenge.common

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class MainModule(val context: Context) {
    @Singleton
    @Provides
    fun provideBackgroundExecutor(): Executor = Executors.newFixedThreadPool(1)

    @Singleton
    @Provides
    fun provideHandlerExecutor(): MainThreadExecutor = MainThreadExecutor()

    @Provides
    fun providesContext(): Context = context

    @Provides
    fun providesResources(): Resources = context.resources
}
