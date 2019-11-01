package fr.ffnet.downloader.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_recipe.view.*

class RecipeAdapter(
    private val recipeListener: RecipeListener,
    private val loadMoreListener: LoadMoreListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var recipeList: List<RecipeDisplayItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object {
        private const val TYPE_LOAD_MORE = 0
        private const val TYPE_RECIPE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_RECIPE) {
            RecipeViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_recipe, parent, false
                ),
                recipeListener
            )
        } else {
            LoadMoreViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_load_more, parent, false
                ),
                loadMoreListener
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (recipeList[position] is RecipeDisplayModel) TYPE_RECIPE else TYPE_LOAD_MORE
    }

    override fun getItemCount(): Int = recipeList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = recipeList[position]) {
            is RecipeDisplayModel -> (holder as RecipeViewHolder).bind(item)
            is RecipeLoadMoreDisplayModel -> (holder as LoadMoreViewHolder).bind(item)
        }
    }
}

class LoadMoreViewHolder(
    private val view: View,
    private val listener: LoadMoreListener
) : RecyclerView.ViewHolder(view) {
    fun bind(loadMoreDisplayModel: RecipeLoadMoreDisplayModel) {
        view.loadMoreTextView.text = loadMoreDisplayModel.title
        view.setOnClickListener {
            listener.onLoadMore()
        }
    }
}

class RecipeViewHolder(
    private val view: View,
    private val listener: RecipeListener
) : RecyclerView.ViewHolder(view) {
    fun bind(recipe: RecipeDisplayModel) {
        view.recipeTitleTextView.text = recipe.title
        Picasso.get().load(recipe.imageUrl).into(view.recipeImageView)
        view.setOnClickListener {
            listener.onRecipeSelected(recipe)
        }
    }
}

interface RecipeListener {
    fun onRecipeSelected(recipe: RecipeDisplayModel)
}

interface LoadMoreListener {
    fun onLoadMore()
}
