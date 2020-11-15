package fr.ffnet.downloader.synced

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.utils.OnFanfictionActionsListener
import kotlinx.android.synthetic.main.item_fanfiction.view.*

class SyncedAdapter(
    private val onActionListener: OnFanfictionActionsListener
) : RecyclerView.Adapter<SyncedAdapter.SyncedViewHolder>() {

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
        holder.bind(fanfictionList[position])
    }

    fun unsync(position: Int) {
        onActionListener.onUnsync(fanfictionList[position])
    }

    inner class SyncedViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(fanfiction: FanfictionSyncedUIModel) {

            view.titleTextView.text = fanfiction.title
            view.syncedChaptersTextView.text = fanfiction.chapters
            view.publishedDateValueTextView.text = fanfiction.publishedDate
            view.updatedDateValueTextView.text = fanfiction.updatedDate

            view.fetchInfoImageView.setOnClickListener {
                onActionListener.onFetchInformation(fanfiction)
            }
            view.exportPdfImageView.setOnClickListener {
                onActionListener.onExportPdf(fanfiction)
            }
            view.exportEpubImageView.setOnClickListener {
                onActionListener.onExportEpub(fanfiction)
            }
        }
    }
}
