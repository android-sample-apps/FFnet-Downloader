package fr.ffnet.downloader.common

import android.app.Application
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjector

interface MainApplicationComponent : AndroidInjector<MainApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MainApplication>()
}

@Module
abstract class MainApplicationModule {
    @Binds
    abstract fun bindsApplication(application: MainApplication): Application
}
