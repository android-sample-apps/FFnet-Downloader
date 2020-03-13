package fr.ffnet.downloader.fanfictionoptions.injection

import dagger.Subcomponent
import fr.ffnet.downloader.fanfictionoptions.OptionsFragment
import fr.ffnet.downloader.fanfictionoptions.OptionsModule

@Subcomponent(
    modules = [
        OptionsModule::class
    ]
)
interface OptionsComponent {
    fun inject(fragment: OptionsFragment)
}