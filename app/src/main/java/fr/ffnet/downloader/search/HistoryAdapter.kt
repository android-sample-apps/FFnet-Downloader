package fr.ffnet.downloader.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryAdapter(
    private val listener: OnHistoryClickListener
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    var historyList: List<HistoryUIModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_history, parent, false
            )
        )
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position], listener)
    }

    inner class HistoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val titleTextView: TextView = view.titleTextView
        private val fetchedDateTextView: TextView = view.fetchedDateTextView

        fun bind(history: HistoryUIModel, listener: OnHistoryClickListener) {
            titleTextView.text = history.title
            fetchedDateTextView.text = history.date
            view.setOnClickListener {
                listener.onHistoryClicked(history.fanfictionId, history.url)
            }
        }
    }

    interface OnHistoryClickListener {
        fun onHistoryClicked(fanfictionId: String, fanfictionUrl: String)
    }
}
