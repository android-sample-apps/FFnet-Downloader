package fr.ffnet.downloader.profile

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.utils.DateFormatter

@Module
class ProfileModule {
    @Provides
    fun provideProfileViewModel(
        fragment: ProfileFragment,
        viewModelCreator: () -> ProfileViewModel
    ): ProfileViewModel {
        val factory = ProfileViewModelFactory(viewModelCreator)
        return ViewModelProvider(fragment.viewModelStore, factory)[ProfileViewModel::class.java]
    }

    @Provides
    fun provideCreator(
        urlTransformer: UrlTransformer,
        dateFormatter: DateFormatter,
        profileRepository: ProfileRepository
    ): () -> ProfileViewModel = {
        ProfileViewModel(
            urlTransformer,
            dateFormatter,
            profileRepository
        )
    }
}
