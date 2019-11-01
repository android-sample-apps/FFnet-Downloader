package fr.ffnet.downloader.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(private val listener: CategoryListener) : RecyclerView.Adapter<CategoryViewHolder>() {

    var categoryList: List<CategoryDisplayModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_category, parent, false
            ),
            listener
        )
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

}

class CategoryViewHolder(
    private val view: View,
    private val listener: CategoryListener
) : RecyclerView.ViewHolder(view) {
    fun bind(category: CategoryDisplayModel) {
        view.categoryTitleTextview.text = category.title
        view.setOnClickListener {
            listener.onCategorySelected(category)
        }
    }
}

interface CategoryListener {
    fun onCategorySelected(category: CategoryDisplayModel)
}
