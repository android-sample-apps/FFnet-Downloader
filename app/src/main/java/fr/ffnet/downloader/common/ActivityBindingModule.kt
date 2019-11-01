package fr.ffnet.downloader.common

import dagger.Module
import dagger.android.ContributesAndroidInjector
import fr.ffnet.downloader.MainActivity
import fr.ffnet.downloader.categories.CategoriesFragment
import fr.ffnet.downloader.categories.CategoriesModule
import fr.ffnet.downloader.category.CategoryActivity
import fr.ffnet.downloader.category.CategoryModule
import fr.ffnet.downloader.recipe.RecipeActivity
import fr.ffnet.downloader.recipe.RecipeModule

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

    @ContributesAndroidInjector(modules = [RecipeModule::class])
    @ActivityScope
    abstract fun contributeRecipeActivityInjector(): RecipeActivity
}
