package fr.ffnet.downloader

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.utils.UrlTransformer
import retrofit2.Retrofit

@Module
class DownloaderModule {

    @Provides
    fun provideDownloaderViewModel(
        interactor: DownloaderInteractor,
        urlTransformer: UrlTransformer,
        resources: Resources
    ): DownloaderViewModel {
        return DownloaderViewModel(interactor, urlTransformer, resources)
    }

    @Provides
    fun provideDownloaderService(retrofit: Retrofit): DownloaderService {
        return retrofit.create(DownloaderService::class.java)
    }

}
