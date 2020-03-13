package fr.ffnet.downloader.profile.injection

import dagger.Subcomponent
import fr.ffnet.downloader.profile.ProfileFragment

@Subcomponent(
    modules = [
        ProfileModule::class
    ]
)
interface ProfileComponent {
    fun inject(fragment: ProfileFragment)
}
