package fr.ffnet.downloader.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import dagger.android.support.DaggerAppCompatActivity
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.activity_category.*
import javax.inject.Inject

class CategoryActivity : DaggerAppCompatActivity(), RecipeListener, LoadMoreListener {

    @Inject lateinit var viewModel: CategoryViewModel

    private val categoryUrl: String by lazy { intent.getStringExtra(EXTRA_CATEGORY_URL) }
    private val adapter: RecipeAdapter by lazy { RecipeAdapter(this, this) }

    companion object {
        private const val DISPLAY_CONTENT = 0
        private const val DISPLAY_NO_CONTENT = 1

        private const val EXTRA_CATEGORY_URL = "EXTRA_CATEGORY_URL"
        fun newIntent(context: Context, categoryUrl: String): Intent = Intent(
            context, CategoryActivity::class.java
        ).apply {
            putExtra(EXTRA_CATEGORY_URL, categoryUrl)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        recipeRecyclerView.adapter = adapter
        viewModel.refreshRecipeList(categoryUrl)
        initObservers()
    }

    override fun onRecipeSelected(recipe: RecipeDisplayModel) {
        Toast.makeText(this, "Selected ${recipe.title}", Toast.LENGTH_SHORT).show()
    }

    override fun onLoadMore() {
        viewModel.loadMore(categoryUrl)
    }

    private fun initObservers() {
        viewModel.getCategoryTitle().observe(this, Observer { title ->
            toolbar.title = title
        })
        viewModel.getRecipeList().observe(this, Observer { recipeList ->
            adapter.recipeList = recipeList
            if (recipeList.isEmpty()) {
                recipeListViewFlipper.displayedChild = DISPLAY_NO_CONTENT
            } else {
                recipeListViewFlipper.displayedChild = DISPLAY_CONTENT
            }
        })
    }
}
