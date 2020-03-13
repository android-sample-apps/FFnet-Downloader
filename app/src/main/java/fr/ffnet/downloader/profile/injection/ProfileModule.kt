package fr.ffnet.downloader.profile.injection

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.profile.ProfileFragment
import fr.ffnet.downloader.profile.ProfileViewModel
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.utils.DateFormatter
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class ProfileModule(private val fragment: Fragment) {

    @Provides
    fun provideProfileViewModel(
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
