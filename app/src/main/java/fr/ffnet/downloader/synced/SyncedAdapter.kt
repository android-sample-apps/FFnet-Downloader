package fr.ffnet.downloader.synced

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.utils.FanfictionAction
import fr.ffnet.downloader.utils.OnActionsClickListener
import kotlinx.android.synthetic.main.item_fanfiction.view.*

class SyncedAdapter(
    private val onMenuItemClickListener: OnActionsClickListener
) : RecyclerView.Adapter<SyncedAdapter.SyncedViewHolder>() {

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
        holder.bind(syncedList[position], onMenuItemClickListener)
    }

    inner class SyncedViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val titleTextView: TextView = view.titleTextView
        private val chaptersTextView: TextView = view.chaptersTextView
        private val publishedDateValueTextView: TextView = view.publishedDateValueTextView
        private val updatedDateValueTextView: TextView = view.updatedDateValueTextView
        private val syncedDateValueTextView: TextView = view.syncedDateValueTextView
        private val actionsImageButton: ImageButton = view.actionsImageButton
        private val widgetVisibilityGroup: Group = view.widgetVisibilityGroup

        fun bind(fanfiction: FanfictionSyncedUIModel, listener: OnActionsClickListener) {
            view.setOnClickListener {
                widgetVisibilityGroup.visibility = if (widgetVisibilityGroup.visibility == View.GONE) View.VISIBLE else View.GONE
            }
            titleTextView.text = fanfiction.title
            chaptersTextView.text = fanfiction.chapters
            publishedDateValueTextView.text = fanfiction.publishedDate
            updatedDateValueTextView.text = fanfiction.updatedDate
            syncedDateValueTextView.text = fanfiction.fetchedDate

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
                popupMenu.inflate(R.menu.synced_fanfictions_menu)
                popupMenu.gravity = Gravity.END
                popupMenu.show()
            }
        }
    }
}