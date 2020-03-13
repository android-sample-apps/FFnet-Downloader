package fr.ffnet.downloader.common

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho

class MainApplication : Application() {

    companion object {
        fun getComponent(
            context: Context
        ): MainComponent = (context.applicationContext as MainApplication).component
    }

    private lateinit var component: MainComponent

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        component = DaggerMainComponent
            .builder()
            .mainModule(MainModule(this))
            .build()
            .apply {
                inject(this@MainApplication)
            }
    }
}
