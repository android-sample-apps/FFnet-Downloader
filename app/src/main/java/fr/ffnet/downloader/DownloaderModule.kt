package fr.ffnet.downloader

import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.utils.UrlTransformer

@Module
class DownloaderModule {

    @Provides
    fun provideDownloaderViewModel(
        interactor: DownloaderInteractor,
        urlTransformer: UrlTransformer
    ): DownloaderViewModel {
        return DownloaderViewModel(interactor, urlTransformer)
    }

}
