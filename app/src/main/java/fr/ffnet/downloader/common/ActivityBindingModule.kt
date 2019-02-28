package fr.ffnet.downloader.common

import dagger.Module
import dagger.android.ContributesAndroidInjector
import fr.ffnet.downloader.MainActivity
import fr.ffnet.downloader.MainActivityModule
import fr.ffnet.downloader.search.SearchFragment
import fr.ffnet.downloader.search.SearchModule

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    @ActivityScope
    abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector(modules = [SearchModule::class])
    @FragmentScope
    abstract fun contributeSearchFragmentInjector(): SearchFragment
}