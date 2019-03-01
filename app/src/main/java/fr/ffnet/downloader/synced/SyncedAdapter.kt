package fr.ffnet.downloader.synced

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.item_fanfiction.view.*

class SyncedAdapter : RecyclerView.Adapter<SyncedAdapter.SyncedViewHolder>() {

    var syncedList: List<FanfictionSyncedUIModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyncedViewHolder {
        return SyncedViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_fanfiction, parent, false
            )
        )
    }

    override fun getItemCount(): Int = syncedList.size

    override fun onBindViewHolder(holder: SyncedViewHolder, position: Int) {
        holder.bind(syncedList[position])
    }

    inner class SyncedViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val titleTextView: TextView = view.titleTextView
        private val chaptersTextView: TextView = view.chaptersTextView
        private val actionsImageButton: ImageButton = view.actionsImageButton

        fun bind(fanfiction: FanfictionSyncedUIModel) {
            titleTextView.text = fanfiction.title
            chaptersTextView.text = fanfiction.chapters
        }
    }

}