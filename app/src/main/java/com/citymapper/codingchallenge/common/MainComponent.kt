package com.citymapper.codingchallenge.common

import com.citymapper.codingchallenge.stoppoints.StopPointsComponent
import com.citymapper.codingchallenge.stoppoints.StopPointsModule
import dagger.Component
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Singleton
@Component(modules = [(MainModule::class), (NetworkModule::class)])
interface MainComponent {
    fun moshiConverter(): MoshiConverterFactory // For integration tests
    fun plus(module: StopPointsModule): StopPointsComponent
}
