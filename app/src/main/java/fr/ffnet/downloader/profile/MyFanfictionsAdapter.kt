package fr.ffnet.downloader.profile

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import fr.ffnet.downloader.utils.FanfictionAction
import fr.ffnet.downloader.utils.OnActionsClickListener
import kotlinx.android.synthetic.main.item_profile_fanfiction.view.*

class MyFanfictionsAdapter(
    private val onMenuItemClickListener: OnActionsClickListener
) : RecyclerView.Adapter<MyFanfictionsAdapter.MyFanfictionsViewHolder>() {

    var fanfictionList: List<FanfictionSyncedUIModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFanfictionsViewHolder {
        return MyFanfictionsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_profile_fanfiction, parent, false
            )
        )
    }

    override fun getItemCount(): Int = fanfictionList.size

    override fun onBindViewHolder(holder: MyFanfictionsViewHolder, position: Int) {
        holder.bind(fanfictionList[position], onMenuItemClickListener)
    }

    inner class MyFanfictionsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val titleTextView: TextView = view.titleTextView
        private val chaptersTextView: TextView = view.chaptersTextView
        private val publishedDateValueTextView: TextView = view.publishedDateValueTextView
        private val updatedDateValueTextView: TextView = view.updatedDateValueTextView
        private val actionsImageButton: ImageButton = view.actionsImageButton

        fun bind(fanfiction: FanfictionSyncedUIModel, listener: OnActionsClickListener) {
            titleTextView.text = fanfiction.title
            chaptersTextView.text = fanfiction.chapters
            publishedDateValueTextView.text = fanfiction.publishedDate
            updatedDateValueTextView.text = fanfiction.updatedDate

            actionsImageButton.setOnClickListener {
                val popupMenu = PopupMenu(actionsImageButton.context, view)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.gotoFanfiction -> listener.onActionClicked(
                            fanfiction.id,
                            FanfictionAction.GOTO_FANFICTION
                        )
                        R.id.exportPdf -> listener.onActionClicked(
                            fanfiction.id,
                            FanfictionAction.EXPORT_PDF
                        )
                        R.id.exportEpub -> listener.onActionClicked(
                            fanfiction.id,
                            FanfictionAction.EXPORT_EPUB
                        )
                        R.id.deleteFanfiction -> listener.onActionClicked(
                            fanfiction.id,
                            FanfictionAction.DELETE_FANFICTION
                        )
                        else -> TODO()
                    }
                    true
                }
                popupMenu.inflate(R.menu.profile_fanfictions_menu)
                popupMenu.gravity = Gravity.END
                popupMenu.show()
            }
        }
    }
}
