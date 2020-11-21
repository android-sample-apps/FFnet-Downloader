package fr.ffnet.downloader.profile.injection

import androidx.fragment.app.Fragment
import dagger.Subcomponent
import fr.ffnet.downloader.profile.AuthorFragment

@Subcomponent(
    modules = [
        ProfileModule::class
    ]
)
interface ProfileComponent {
    fun inject(fragment: Fragment)
}
