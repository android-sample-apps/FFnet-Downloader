package fr.ffnet.downloader.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.utils.OnFanfictionOptionsListener
import kotlinx.android.synthetic.main.item_fanfiction.view.*

class FanfictionAdapter(
    private val onMenuItemClickListener: OnFanfictionOptionsListener
) : RecyclerView.Adapter<FanfictionAdapter.SyncedViewHolder>() {

    var fanfictionList: List<FanfictionSyncedUIModel> = emptyList()
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

    override fun getItemCount(): Int = fanfictionList.size

    override fun onBindViewHolder(holder: SyncedViewHolder, position: Int) {
        holder.bind(fanfictionList[position], onMenuItemClickListener)
    }

    inner class SyncedViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val titleTextView: TextView = view.titleTextView
        private val chaptersTextView: TextView = view.chaptersTextView

        fun bind(fanfiction: FanfictionSyncedUIModel, listener: OnFanfictionOptionsListener) {
            view.setOnClickListener {
                listener.onOptionsClicked(fanfiction.id)
            }
            titleTextView.text = fanfiction.title
            chaptersTextView.text = fanfiction.chapters
        }
    }
}
