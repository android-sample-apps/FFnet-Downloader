package fr.ffnet.downloader.profile

import dagger.Subcomponent

@Subcomponent(
    modules = [
        AuthorModule::class
    ]
)
interface AuthorComponent {
    fun inject(fragment: AuthorFragment)
}
