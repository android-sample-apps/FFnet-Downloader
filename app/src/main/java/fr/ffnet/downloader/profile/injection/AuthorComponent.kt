package fr.ffnet.downloader.profile.injection

import dagger.Subcomponent
import fr.ffnet.downloader.profile.AuthorModule

@Subcomponent(
    modules = [
        AuthorModule::class
    ]
)
interface AuthorComponent {
    fun inject(fragment: AuthorFragment)
}
