package fr.ffnet.downloader.synced.injection

import dagger.Subcomponent
import fr.ffnet.downloader.synced.SyncedFragment

@Subcomponent(
    modules = [
        SyncedModule::class
    ]
)
interface SyncedComponent {

    fun inject(fragment: SyncedFragment)
}
