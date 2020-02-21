package fr.ffnet.downloader.profile

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class ProfileModule {
    @Provides
    fun provideProfileViewModel(
        fragment: ProfileFragment,
        urlTransformer: UrlTransformer,
        dateFormatter: DateFormatter,
        profileRepository: ProfileRepository
    ): ProfileViewModel {
        val factory = ViewModelFactory {
            ProfileViewModel(
                urlTransformer,
                dateFormatter,
                profileRepository
            )
        }
        return ViewModelProvider(fragment, factory)[ProfileViewModel::class.java]
    }
}
