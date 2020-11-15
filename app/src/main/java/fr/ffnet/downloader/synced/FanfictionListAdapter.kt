package fr.ffnet.downloader.synced

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.utils.OnFanfictionActionsListener
import kotlinx.android.synthetic.main.item_fanfiction.view.*

class FanfictionListAdapter(
    private val onActionListener: OnFanfictionActionsListener
) : RecyclerView.Adapter<FanfictionListAdapter.FanfictionItemViewHolder>() {

    var fanfictionList: List<FanfictionSyncedUIModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FanfictionItemViewHolder {
        return FanfictionItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_fanfiction, parent, false
            )
        )
    }

    override fun getItemCount(): Int = fanfictionList.size

    override fun onBindViewHolder(holder: FanfictionItemViewHolder, position: Int) {
        holder.bind(fanfictionList[position])
    }

    fun unsync(position: Int) {
        onActionListener.onUnsync(fanfictionList[position])
    }

    inner class FanfictionItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(fanfiction: FanfictionSyncedUIModel) {

            view.titleTextView.text = fanfiction.title
            view.syncedChaptersTextView.text = fanfiction.progressionText
            view.publishedDateValueTextView.text = fanfiction.publishedDate
            view.updatedDateValueTextView.text = fanfiction.updatedDate

            view.exportPdfImageView.setBackgroundResource(fanfiction.exportPdfImage)
            view.exportEpubImageView.setBackgroundResource(fanfiction.exportEpubImage)

            view.fetchInfoImageView.setOnClickListener {
                onActionListener.onFetchInformation(fanfiction)
            }
            if (fanfiction.isDownloadComplete) {
                view.exportPdfImageView.setOnClickListener {
                    onActionListener.onExportPdf(fanfiction.id)
                }
                view.exportEpubImageView.setOnClickListener {
                    onActionListener.onExportEpub(fanfiction.id)
                }
            }
        }
    }
}
