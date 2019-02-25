package fr.ffnet.downloader.downloader

import dagger.Subcomponent
import fr.ffnet.downloader.common.FeatureScope

@FeatureScope
@Subcomponent(modules = [DownloaderModule::class])
interface DownloaderComponent {

    fun inject(activity: DownloaderActivity)

}
