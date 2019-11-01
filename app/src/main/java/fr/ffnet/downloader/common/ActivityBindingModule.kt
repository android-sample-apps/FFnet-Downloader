package fr.ffnet.downloader.common

import dagger.Module
import dagger.android.ContributesAndroidInjector
import fr.ffnet.downloader.MainActivity
import fr.ffnet.downloader.categories.CategoriesFragment
import fr.ffnet.downloader.categories.CategoriesModule
import fr.ffnet.downloader.category.CategoryActivity
import fr.ffnet.downloader.category.CategoryModule

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    @ActivityScope
    abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector(modules = [CategoriesModule::class])
    @FragmentScope
    abstract fun contributeSearchFragmentInjector(): CategoriesFragment

    @ContributesAndroidInjector(modules = [CategoryModule::class])
    @ActivityScope
    abstract fun contributeCategoryActivityInjector(): CategoryActivity
}
