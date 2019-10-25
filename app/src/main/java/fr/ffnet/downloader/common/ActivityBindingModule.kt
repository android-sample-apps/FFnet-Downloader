package fr.ffnet.downloader.common

import dagger.Module
import dagger.android.ContributesAndroidInjector
import fr.ffnet.downloader.MainActivity
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.fanfiction.FanfictionModule
import fr.ffnet.downloader.profile.ProfileFragment
import fr.ffnet.downloader.profile.ProfileModule
import fr.ffnet.downloader.profile.fanfiction.ProfileFanfictionFragment
import fr.ffnet.downloader.profile.fanfiction.ProfileFanfictionModule
import fr.ffnet.downloader.search.SearchFragment
import fr.ffnet.downloader.search.SearchModule

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    @ActivityScope
    abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector(modules = [FanfictionModule::class])
    @ActivityScope
    abstract fun contributeFanfictionActivityInjector(): FanfictionActivity

    @ContributesAndroidInjector(modules = [SearchModule::class])
    @FragmentScope
    abstract fun contributeSearchFragmentInjector(): SearchFragment

    @ContributesAndroidInjector(modules = [ProfileModule::class])
    @FragmentScope
    abstract fun contributeProfileFragmentInjector(): ProfileFragment

    @ContributesAndroidInjector(modules = [ProfileFanfictionModule::class])
    @FragmentScope
    abstract fun contributeProfileFanfictionFragmentInjector(): ProfileFanfictionFragment
}
