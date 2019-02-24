package fr.ffnet.downloader.common

import dagger.Component
import fr.ffnet.downloader.DownloaderComponent
import fr.ffnet.downloader.DownloaderModule
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MainModule::class,
        NetworkModule::class
    ]
)
interface MainComponent {
    fun moshiConverter(): MoshiConverterFactory // For integration tests
    fun plus(module: DownloaderModule): DownloaderComponent
}