package fr.ffnet.downloader.synced

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.synced.FanfictionUIItem.FanfictionUI
import fr.ffnet.downloader.synced.FanfictionUIItem.FanfictionUITitle
import fr.ffnet.downloader.synced.FanfictionUIItem.HistoryUI
import fr.ffnet.downloader.utils.OnFanfictionActionsListener
import kotlinx.android.synthetic.main.item_fanfiction.view.*
import kotlinx.android.synthetic.main.item_fanfiction.view.titleTextView
import kotlinx.android.synthetic.main.item_fanfiction_title.view.*
import kotlinx.android.synthetic.main.item_history.view.*

interface OnSyncAllFanfictionsListener {
    fun onSyncAll()
}

interface OnHistoryClickListener {
    fun onHistoryClicked(fanfictionId: String, fanfictionUrl: String)
}

class FanfictionListAdapter(
    private val onActionListener: OnFanfictionActionsListener,
    private val syncAllListener: OnSyncAllFanfictionsListener? = null,
    private val historyListener: OnHistoryClickListener? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_FANFICTION = 1
        private const val TYPE_HISTORY = 2
    }

    var fanfictionItemList: List<FanfictionUIItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> FanfictionUITitleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_fanfiction_title, parent, false
                )
            )
            TYPE_HISTORY -> HistoryUIViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_history, parent, false
                )
            )
            else -> FanfictionUIViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_fanfiction, parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int =
        when {
            fanfictionItemList[position] is FanfictionUITitle -> TYPE_HEADER
            fanfictionItemList[position] is HistoryUI -> TYPE_HISTORY
            else -> TYPE_FANFICTION
        }

    override fun getItemCount(): Int = fanfictionItemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = fanfictionItemList[position]) {
            is FanfictionUITitle -> (holder as FanfictionUITitleViewHolder).bind(item)
            is HistoryUI -> (holder as HistoryUIViewHolder).bind(item, historyListener)
            is FanfictionUI -> (holder as FanfictionUIViewHolder).bind(item)
        }
    }

    fun unsync(position: Int) {
        onActionListener.onUnsync(fanfictionItemList[position] as FanfictionUI)
    }

    inner class FanfictionUITitleViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: FanfictionUITitle) {
            view.historyUITitleTextView.text = item.title
            view.syncAllFanfictionsImageView.isVisible = item.shouldShowSyncAllButton
            view.syncAllFanfictionsImageView.setOnClickListener {
                syncAllListener?.onSyncAll()
            }
        }
    }

    inner class HistoryUIViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(history: HistoryUI, listener: OnHistoryClickListener?) {
            view.titleTextView.text = history.title
            view.fetchedDateTextView.text = history.date
            view.setOnClickListener {
                listener?.onHistoryClicked(history.fanfictionId, history.url)
            }
        }
    }

    inner class FanfictionUIViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(fanfiction: FanfictionUI) {

            view.titleTextView.text = fanfiction.title

            view.syncedChaptersTextView.text = fanfiction.progressionText
            view.syncedChaptersTextView.isVisible = fanfiction.shouldShowChapterSync

            view.publishedDateValueTextView.text = fanfiction.publishedDate
            view.updatedDateValueTextView.text = fanfiction.updatedDate

            view.exportPdfImageView.setBackgroundResource(fanfiction.exportPdfImage)
            view.exportEpubImageView.setBackgroundResource(fanfiction.exportEpubImage)

            view.authorValueTextView.text = fanfiction.author
            view.authorLabelTextView.isVisible = fanfiction.shouldShowAuthor
            view.authorValueTextView.isVisible = fanfiction.shouldShowAuthor

            view.wordsValueTextView.text = fanfiction.words
            view.wordsLabelTextView.isVisible = fanfiction.shouldShowWords
            view.wordsValueTextView.isVisible = fanfiction.shouldShowWords

            view.chaptersValueTextView.text = fanfiction.chaptersNb.toString()
            view.chaptersLabelTextView.isVisible = fanfiction.shouldShowChapterSync.not()
            view.chaptersValueTextView.isVisible = fanfiction.shouldShowChapterSync.not()

            view.setOnClickListener {
                onActionListener.onFetchInformation(fanfiction)
            }

            view.exportPdfImageView.isVisible = fanfiction.shouldShowExport
            view.exportEpubImageView.isVisible = fanfiction.shouldShowExport
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
