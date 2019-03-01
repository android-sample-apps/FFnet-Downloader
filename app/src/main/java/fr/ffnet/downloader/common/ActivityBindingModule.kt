package fr.ffnet.downloader.common

import dagger.Module
import dagger.android.ContributesAndroidInjector
import fr.ffnet.downloader.MainActivity
import fr.ffnet.downloader.MainActivityModule
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.fanfiction.FanfictionModule
import fr.ffnet.downloader.search.SearchFragment
import fr.ffnet.downloader.search.SearchModule
import fr.ffnet.downloader.synced.SyncedFragment
import fr.ffnet.downloader.synced.SyncedModule

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    @ActivityScope
    abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector(modules = [SearchModule::class])
    @FragmentScope
    abstract fun contributeSearchFragmentInjector(): SearchFragment

    @ContributesAndroidInjector(modules = [SyncedModule::class])
    @FragmentScope
    abstract fun contributeSyncedFragmentInjector(): SyncedFragment

    @ContributesAndroidInjector(modules = [FanfictionModule::class])
    @ActivityScope
    abstract fun contributeFanfictionActivityInjector(): FanfictionActivity
}