package fr.ffnet.downloader.search.injection

import dagger.Subcomponent
import fr.ffnet.downloader.search.SearchFragment

@Subcomponent(
    modules = [
        SearchModule::class
    ]
)
interface SearchComponent {

    fun inject(fragment: SearchFragment)
}
