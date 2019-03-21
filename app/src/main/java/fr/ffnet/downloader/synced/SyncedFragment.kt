package fr.ffnet.downloader.synced

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.FanfictionOptionsDialogFragment
import fr.ffnet.downloader.FanfictionOptionsDialogFragment.Companion.EXTRA_ACTION_DELETE
import fr.ffnet.downloader.FanfictionOptionsDialogFragment.Companion.EXTRA_ACTION_DETAILS
import fr.ffnet.downloader.FanfictionOptionsDialogFragment.Companion.EXTRA_ACTION_EPUB
import fr.ffnet.downloader.FanfictionOptionsDialogFragment.Companion.EXTRA_ACTION_PDF
import fr.ffnet.downloader.FanfictionOptionsDialogFragment.Companion.EXTRA_FANFICTION_ID
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.ViewModelFactory
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.synced.SyncedViewModel.SyncedFanfictionsResult
import fr.ffnet.downloader.utils.OnFanfictionOptionsListener
import kotlinx.android.synthetic.main.fragment_synced.*
import javax.inject.Inject

class SyncedFragment : DaggerFragment(), OnFanfictionOptionsListener {

    private lateinit var viewModel: SyncedViewModel
    @Inject lateinit var viewModelFactory: ViewModelFactory<SyncedViewModel>

    companion object {
        private const val DISPLAY_SYNCED_FANFICTIONS = 0
        private const val DISPLAY_NO_SYNCED_FANFICTIONS = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_synced, container, false).also {
            activity?.title = resources.getString(R.string.synced_title)
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(
                SyncedViewModel::class.java
            )
            viewModel.loadFanfictionsFromDb()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        viewModel.getFanfictionList().observe(this, Observer { fanfictionResult ->
            when (fanfictionResult) {
                is SyncedFanfictionsResult.NoSyncedFanfictions -> showNoSyncedFanfictions()
                is SyncedFanfictionsResult.SyncedFanfictions -> showSyncedFanfictions(
                    fanfictionResult.fanfictionList
                )
            }
        })
    }

    override fun onOptionsClicked(fanfictionId: String, title: String) {
        val optionsFragment = FanfictionOptionsDialogFragment.newInstance(fanfictionId, title)
        optionsFragment.setTargetFragment(this, 1000)
        fragmentManager?.let {
            optionsFragment.show(it, "fanfiction_options")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                val fanfictionId = intent.getStringExtra(EXTRA_FANFICTION_ID)
                when (intent.getStringExtra(FanfictionOptionsDialogFragment.EXTRA_ACTION)) {
                    EXTRA_ACTION_DETAILS -> startFanfictionActivity(fanfictionId)
                    EXTRA_ACTION_PDF -> println("EXTRA_ACTION_PDF")
                    EXTRA_ACTION_EPUB -> println("EXTRA_ACTION_EPUB")
                    EXTRA_ACTION_DELETE -> viewModel.unsyncFanfiction(fanfictionId)
                }
            }
        }
    }

    private fun exportPdf(fanfictionId: String) {

    }

    private fun showSyncedFanfictions(fanfictionList: List<FanfictionSyncedUIModel>) {
        (syncedFanfictionsRecyclerView.adapter as SyncedAdapter).syncedList = fanfictionList
        fanfictionResultViewFlipper.displayedChild = DISPLAY_SYNCED_FANFICTIONS
    }

    private fun showNoSyncedFanfictions() {
        fanfictionResultViewFlipper.displayedChild = DISPLAY_NO_SYNCED_FANFICTIONS
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