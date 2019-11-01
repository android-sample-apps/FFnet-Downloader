package fr.ffnet.downloader.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.item_recipe.view.*

class RecipeAdapter(
    private val listener: RecipeListener
) : RecyclerView.Adapter<RecipeViewHolder>() {

    var recipeList: List<RecipeDisplayModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_recipe, parent, false
            ),
            listener
        )
    }

    override fun getItemCount(): Int = recipeList.size

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipeList[position])
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
