package fr.ffnet.downloader.fanfiction.injection

import dagger.Subcomponent
import fr.ffnet.downloader.fanfiction.FanfictionActivity

@Subcomponent(
    modules = [
        FanfictionModule::class
    ]
)
interface FanfictionComponent {
    fun inject(activity: FanfictionActivity)
}
