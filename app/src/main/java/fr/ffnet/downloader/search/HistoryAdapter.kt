package fr.ffnet.downloader.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.search.HistoryItem.HistoryUI
import fr.ffnet.downloader.search.HistoryItem.HistoryUITitle
import kotlinx.android.synthetic.main.item_history.view.*
import kotlinx.android.synthetic.main.item_history_title.view.*

class HistoryAdapter(
    private val listener: OnHistoryClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTENT = 1
    }

    var historyList: List<HistoryItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HistoryUITitleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_history_title, parent, false
                )
            )
            else -> HistoryUIViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_history, parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int =
        when {
            historyList[position] is HistoryUITitle -> TYPE_HEADER
            else -> TYPE_CONTENT
        }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = historyList[position]) {
            is HistoryUITitle -> (holder as HistoryUITitleViewHolder).bind(item.title)
            is HistoryUI -> (holder as HistoryUIViewHolder).bind(item, listener)
        }
    }

    inner class HistoryUITitleViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(title: String) {
            view.historyUITitleTextView.text = title
        }
    }

    inner class HistoryUIViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(history: HistoryUI, listener: OnHistoryClickListener) {
            view.titleTextView.text = history.title
            view.fetchedDateTextView.text = history.date
            view.setOnClickListener {
                listener.onHistoryClicked(history.fanfictionId, history.url)
            }
        }
    }

    interface OnHistoryClickListener {
        fun onHistoryClicked(fanfictionId: String, fanfictionUrl: String)
    }
}
