package fr.ffnet.downloader

import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.ErrorRepository

@Module
class MainActivityModule {

    @Provides
    fun provideMainViewModel(repository: ErrorRepository): MainViewModel = MainViewModel(
        repository
    )
}
