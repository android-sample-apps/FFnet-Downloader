package fr.ffnet.downloader.common

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import timber.log.Timber
import javax.inject.Inject

class MainApplication : Application() {

    private lateinit var component: MainComponent
    @Inject lateinit var logger: FFLogger

    companion object {
        fun getComponent(
            context: Context
        ): MainComponent = (context.applicationContext as MainApplication).component
    }

    override fun onCreate() {
        super.onCreate()

        component = DaggerMainComponent
            .builder()
            .mainModule(MainModule(this))
            .build()
            .apply {
                inject(this@MainApplication)
            }

        Stetho.initializeWithDefaults(this)
        Timber.plant(Timber.DebugTree())
        FFLogger.init(logger)
    }
}
