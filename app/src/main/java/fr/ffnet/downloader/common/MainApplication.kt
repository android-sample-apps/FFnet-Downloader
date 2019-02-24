package fr.ffnet.downloader.common

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen

class MainApplication : Application() {

    private lateinit var mainComponent: MainComponent

    companion object {
        fun getComponent(context: Context): MainComponent = (context.applicationContext as MainApplication).mainComponent
    }

    override fun onCreate() {
        super.onCreate()
        mainComponent = DaggerMainComponent.builder().mainModule(MainModule(this)).build()
        AndroidThreeTen.init(this)
    }
}
