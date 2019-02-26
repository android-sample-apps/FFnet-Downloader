package fr.ffnet.downloader.downloader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.item_chapter.view.*


class ChapterListAdapter(
    private val listener: ChapterClickListener
) : RecyclerView.Adapter<ChapterListAdapter.ChapterViewHolder>() {

    var chapterList: List<ChapterViewModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        return ChapterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chapter, parent, false
            )
        )
    }

    override fun getItemCount(): Int = chapterList.size

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(chapterList[position], listener)
    }

    inner class ChapterViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val chapterNbTextView: TextView = view.chapterNbTextView
        private val chapterTitleTextView: TextView = view.chapterTitleTextView
        private val chapterStatusTextView: TextView = view.chapterStatusTextView

        fun bind(chapterViewModel: ChapterViewModel, listener: ChapterClickListener) {
            chapterNbTextView.text = chapterViewModel.id
            chapterTitleTextView.text = chapterViewModel.title
            chapterStatusTextView.text = chapterViewModel.status
            view.setOnClickListener {
                listener.onChapterSelected(chapterViewModel)
            }
        }
    }

    interface ChapterClickListener {
        fun onChapterSelected(chapter: ChapterViewModel)
    }
}
