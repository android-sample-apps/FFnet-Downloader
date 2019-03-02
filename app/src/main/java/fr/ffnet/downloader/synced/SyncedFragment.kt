package fr.ffnet.downloader.synced

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import kotlinx.android.synthetic.main.fragment_synced.*
import javax.inject.Inject

class SyncedFragment : DaggerFragment(), SyncedAdapter.OnActionsClickListener {

    @Inject lateinit var viewModel: SyncedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_synced, container, false).also {
            viewModel.loadFanfictionsFromDb()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        viewModel.getFanfictionList().observe(this, Observer { syncedList ->
            (syncedFanfictionsRecyclerView.adapter as SyncedAdapter).syncedList = syncedList
        })
    }

    override fun onActionClicked(
        fanfictionId: String,
        action: SyncedAdapter.FanfictionAction
    ) {
        when (action) {
            SyncedAdapter.FanfictionAction.GOTO_FANFICTION -> startFanfictionActivity(fanfictionId)
            SyncedAdapter.FanfictionAction.EXPORT_PDF -> Log.d("ACTION", "EXPORT_PDF")
            SyncedAdapter.FanfictionAction.EXPORT_EPUB -> Log.d("ACTION", "EXPORT_EPUB")
            SyncedAdapter.FanfictionAction.DELETE_FANFICTION -> {
                viewModel.deleteFanfiction(fanfictionId)
            }
        }
    }

    private fun startFanfictionActivity(fanfictionId: String) {
        context?.let { context ->
            startActivity(FanfictionActivity.intent(context, fanfictionId))
        }
    }

    private fun initRecyclerView() {
        syncedFanfictionsRecyclerView.layoutManager = LinearLayoutManager(context)
        syncedFanfictionsRecyclerView.adapter = SyncedAdapter(this)
    }
}