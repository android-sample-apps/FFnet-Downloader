package fr.ffnet.downloader.profile

import androidx.lifecycle.ViewModelProviders
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
        return ViewModelProviders.of(fragment, factory)[ProfileViewModel::class.java]
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
