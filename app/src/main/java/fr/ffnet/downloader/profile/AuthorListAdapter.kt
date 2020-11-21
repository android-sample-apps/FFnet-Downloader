package fr.ffnet.downloader.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.profile.AuthorUIItem.AuthorTitleUIItem
import fr.ffnet.downloader.profile.AuthorUIItem.SearchAuthorNotResultUIItem
import fr.ffnet.downloader.profile.AuthorUIItem.SearchAuthorUIItem
import fr.ffnet.downloader.profile.AuthorUIItem.SyncedAuthorUIItem
import kotlinx.android.synthetic.main.item_author.view.*
import kotlinx.android.synthetic.main.item_author_title.view.*
import kotlinx.android.synthetic.main.item_no_result.view.*

interface OnAuthorListener {
    fun onLoadAuthor(authorId: String)
    fun onUnsync(authorId: SyncedAuthorUIItem)
}

class AuthorListAdapter(
    private val authorListener: OnAuthorListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_AUTHOR = 1
        private const val TYPE_SEARCH = 2
        private const val TYPE_SEARCH_EMPTY = 3
    }

    var authorItemList: List<AuthorUIItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> TitleAuthorViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_author_title, parent, false
                )
            )
            TYPE_AUTHOR -> SyncedAuthorViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_author, parent, false
                )
            )
            TYPE_SEARCH_EMPTY -> NoResultViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_no_result, parent, false
                )
            )
            else -> SearchAuthorViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_author, parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int = when {
        authorItemList[position] is AuthorTitleUIItem -> TYPE_HEADER
        authorItemList[position] is SyncedAuthorUIItem -> TYPE_AUTHOR
        authorItemList[position] is SearchAuthorNotResultUIItem -> TYPE_SEARCH_EMPTY
        else -> TYPE_SEARCH
    }

    override fun getItemCount(): Int = authorItemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = authorItemList[position]) {
            is AuthorTitleUIItem -> (holder as TitleAuthorViewHolder).bind(item)
            is SyncedAuthorUIItem -> (holder as SyncedAuthorViewHolder).bind(item)
            is SearchAuthorUIItem -> (holder as SearchAuthorViewHolder).bind(item)
            is SearchAuthorNotResultUIItem -> (holder as NoResultViewHolder).bind(item)
        }
    }

    fun unsync(position: Int) {
        authorListener.onUnsync(authorItemList[position] as SyncedAuthorUIItem)
    }

    inner class NoResultViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: SearchAuthorNotResultUIItem) {
            view.noResultTextView.text = item.message
        }
    }

    inner class TitleAuthorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: AuthorTitleUIItem) {
            view.authorUITitleTextView.text = item.title
        }
    }

    inner class SyncedAuthorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(author: SyncedAuthorUIItem) {
            view.nameTextView.text = author.name
            view.favoritesNbValueTextView.text = author.nbFavorites
            view.storiesNbValueTextView.text = author.nbStories
            view.fetchedDateValueTextView.text = author.fetchedDate

            view.setOnClickListener {
                authorListener.onLoadAuthor(author.id)
            }
        }
    }

    inner class SearchAuthorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(author: SearchAuthorUIItem) {
            view.nameTextView.text = author.name

            view.favoritesNbLabelTextView.isVisible = false
            view.favoritesNbValueTextView.isVisible = false

            view.storiesNbValueTextView.text = author.nbStories

            view.fetchedDateLabelTextView.isVisible = false
            view.fetchedDateValueTextView.isVisible = false

            view.setOnClickListener {
                authorListener.onLoadAuthor(author.id)
            }
        }
    }
}
