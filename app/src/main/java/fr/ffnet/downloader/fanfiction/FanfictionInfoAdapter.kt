package fr.ffnet.downloader.fanfiction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.item_chapter.view.*
import kotlinx.android.synthetic.main.item_fanfiction_header.view.*

class FanfictionInfoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var fanfictionInfoList: List<FanfictionInfoUIModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object {
        private const val HEADER_VIEW_TYPE = 1
        private const val CHAPTER_VIEW_TYPE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (fanfictionInfoList[position]) {
            is FanfictionInfoUIModel.FanfictionUIModel -> HEADER_VIEW_TYPE
            is FanfictionInfoUIModel.ChapterUIModel -> CHAPTER_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER_VIEW_TYPE -> FanfictionInfoHeader(
                inflater.inflate(R.layout.item_fanfiction_header, parent, false)
            )
            CHAPTER_VIEW_TYPE -> FanfictionInfoChapter(
                inflater.inflate(R.layout.item_chapter, parent, false)
            )
            else -> TODO()
        }
    }

    override fun getItemCount(): Int = fanfictionInfoList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            HEADER_VIEW_TYPE -> (holder as FanfictionInfoHeader).bind(
                fanfictionInfoList[position] as FanfictionInfoUIModel.FanfictionUIModel
            )
            CHAPTER_VIEW_TYPE -> (holder as FanfictionInfoChapter).bind(
                fanfictionInfoList[position] as FanfictionInfoUIModel.ChapterUIModel
            )
        }
    }

    inner class FanfictionInfoHeader(view: View) : RecyclerView.ViewHolder(view) {

        private val widgetVisibilityGroup: Group = view.widgetVisibilityGroup
        private val titleValueTextView: TextView = view.titleValueTextView
        private val wordsValueTextView: TextView = view.wordsValueTextView
        private val publishedDateValueTextView: TextView = view.publishedDateValueTextView
        private val updatedDateValueTextView: TextView = view.updatedDateValueTextView
        private val syncedDateValueTextView: TextView = view.syncedDateValueTextView

        fun bind(fanfictionInfo: FanfictionInfoUIModel.FanfictionUIModel) {
            widgetVisibilityGroup.visibility = View.VISIBLE
            titleValueTextView.text = fanfictionInfo.title
            wordsValueTextView.text = fanfictionInfo.words
            publishedDateValueTextView.text = fanfictionInfo.publishedDate
            updatedDateValueTextView.text = fanfictionInfo.updatedDate
            syncedDateValueTextView.text = fanfictionInfo.syncedDate
        }
    }

    inner class FanfictionInfoChapter(view: View) : RecyclerView.ViewHolder(view) {

        private val chapterNbTextView: TextView = view.chapterNbTextView
        private val chapterTitleTextView: TextView = view.chapterTitleTextView
        private val chapterStatusTextView: TextView = view.chapterStatusTextView

        fun bind(chapter: FanfictionInfoUIModel.ChapterUIModel) {
            chapterNbTextView.text = chapter.id
            chapterTitleTextView.text = chapter.title
            chapterStatusTextView.text = chapter.status
        }
    }

}
