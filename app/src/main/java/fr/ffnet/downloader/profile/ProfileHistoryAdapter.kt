package fr.ffnet.downloader.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.item_history.view.*

class ProfileHistoryAdapter(
    private val listener: OnHistoryClickListener
) : RecyclerView.Adapter<ProfileHistoryAdapter.HistoryViewHolder>() {

    var historyList: List<ProfileUIModel> = emptyList()
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

        private val titleTextView: TextView = view.nameTextView
        private val fetchedDateTextView: TextView = view.fetchedDateTextView

        fun bind(history: ProfileUIModel, listener: OnHistoryClickListener) {
            titleTextView.text = history.name
            fetchedDateTextView.text = history.fetchedDate
            view.setOnClickListener {
                listener.onHistoryClicked(history.profileId, history.url)
            }
        }
    }

    interface OnHistoryClickListener {
        fun onHistoryClicked(profileId: String, profileUrl: String)
    }
}
