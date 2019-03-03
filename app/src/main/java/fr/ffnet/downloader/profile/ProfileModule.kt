package fr.ffnet.downloader.profile

import android.content.SharedPreferences
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.fanfictionutils.UrlTransformer
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.ProfileRepository
import fr.ffnet.downloader.repository.RepositoryModule

@Module(includes = [RepositoryModule::class])
class ProfileModule {
    @FragmentScope
    @Provides
    fun provideProfileViewModel(
        urlTransformer: UrlTransformer,
        resources: Resources,
        sharedPreferences: SharedPreferences,
        databaseRepository: DatabaseRepository,
        profileRepository: ProfileRepository
    ): ProfileViewModel = ProfileViewModel(
        urlTransformer,
        resources,
        sharedPreferences,
        databaseRepository,
        profileRepository
    )
}
