package fr.ffnet.downloader.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.synced.FanfictionSyncedUIModel
import kotlinx.android.synthetic.main.item_fanfiction_view_pager_adapter.view.*

class FanfictionViewPagerAdapter : RecyclerView.Adapter<FanfictionViewPagerAdapter.FanfictionViewPagerHolder>() {

    var adapterList: List<Pair<List<FanfictionSyncedUIModel>, MyFanfictionsAdapter>> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FanfictionViewPagerHolder {
        return FanfictionViewPagerHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_fanfiction_view_pager_adapter, parent, false
            )
        )
    }

    override fun getItemCount(): Int = adapterList.size

    override fun onBindViewHolder(holder: FanfictionViewPagerHolder, position: Int) {
        holder.bind(adapterList[position])
    }

    inner class FanfictionViewPagerHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val recyclerView: RecyclerView = view.fanfictionRecyclerView
        private val profileFanfictionsViewFlipper: ViewFlipper = view.profileFanfictionsViewFlipper

        fun bind(adapter: Pair<List<FanfictionSyncedUIModel>, MyFanfictionsAdapter>) {
            profileFanfictionsViewFlipper.displayedChild = if (adapter.first.isNotEmpty()) 1 else 0
            recyclerView.layoutManager = LinearLayoutManager(view.context)
            recyclerView.adapter = adapter.second
            (recyclerView.adapter as MyFanfictionsAdapter).fanfictionList = adapter.first
        }
    }
}
