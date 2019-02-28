package fr.ffnet.downloader.common

import android.content.Context
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class MainApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerMainApplicationComponent.builder().create(this)
    }

    private lateinit var mainComponent: MainComponent

    companion object {
        fun getComponent(context: Context): MainComponent = (context.applicationContext as MainApplication).mainComponent
    }

    override fun onCreate() {
        super.onCreate()
        mainComponent = DaggerMainComponent.builder().mainModule(MainModule(this)).build()
        Stetho.initializeWithDefaults(this)
    }
}
