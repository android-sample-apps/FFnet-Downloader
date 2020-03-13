package fr.ffnet.downloader.profile.fanfiction.injection

import dagger.Subcomponent
import fr.ffnet.downloader.profile.fanfiction.ProfileFanfictionFragment

@Subcomponent(
    modules = [
        ProfileFanfictionModule::class
    ]
)
interface ProfileFanfictionComponent {

    fun inject(fragment: ProfileFanfictionFragment)
}
