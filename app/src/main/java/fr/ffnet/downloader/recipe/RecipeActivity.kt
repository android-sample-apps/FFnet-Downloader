package fr.ffnet.downloader.recipe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import dagger.android.support.DaggerAppCompatActivity
import fr.ffnet.downloader.R
import javax.inject.Inject

class RecipeActivity : DaggerAppCompatActivity() {

    companion object {
        private const val EXTRA_RECIPE_URL = "EXTRA_RECIPE_URL"
        fun newIntent(context: Context, recipeUrl: String): Intent =
            Intent(context, RecipeActivity::class.java).apply {
                putExtra(EXTRA_RECIPE_URL, recipeUrl)
            }
    }

    @Inject lateinit var viewModel: RecipeViewModel
    private val recipeUrl: String by lazy { intent.getStringExtra(EXTRA_RECIPE_URL) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
    }

    private fun initObservers() {
        viewModel.getRecipeTitle().observe(this, Observer {

        })
    }
}
