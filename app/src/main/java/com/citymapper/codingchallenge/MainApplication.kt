package com.citymapper.codingchallenge

import android.app.Application
import android.content.Context
import com.citymapper.codingchallenge.common.DaggerMainComponent
import com.citymapper.codingchallenge.common.MainComponent
import com.citymapper.codingchallenge.common.MainModule

class MainApplication : Application() {

    private lateinit var mainComponent: MainComponent

    companion object {
        fun getComponent(context: Context): MainComponent = (context.applicationContext as MainApplication).mainComponent
    }

    override fun onCreate() {
        super.onCreate()
        mainComponent = DaggerMainComponent.builder().mainModule(MainModule(this)).build()
    }
}
