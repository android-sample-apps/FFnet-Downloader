package fr.ffnet.downloader.synced

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.synced.SyncedViewModel.SyncedFanfictionsResult
import fr.ffnet.downloader.synced.injection.SyncedModule
import fr.ffnet.downloader.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_synced.*
import javax.inject.Inject

class SyncedFragment : Fragment() {

    @Inject lateinit var viewModel: SyncedViewModel
    @Inject lateinit var optionsController: OptionsController

    companion object {
        private const val DISPLAY_SYNCED_FANFICTIONS = 0
        private const val DISPLAY_NO_SYNCED_FANFICTIONS = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = resources.getString(R.string.synced_title)
        return inflater.inflate(R.layout.fragment_synced, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(SyncedModule(this))
            .inject(this)

        swipeRefresh.setOnRefreshListener {
            viewModel.refreshSyncedInfo()
        }

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        initializeAdapter()
        setObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.synced_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.syncUnsynced -> {
                viewModel.syncAllUnsyncedChapters()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeAdapter() {
        syncedFanfictionsRecyclerView.adapter = SyncedAdapter(optionsController)
        val swiper = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (syncedFanfictionsRecyclerView.adapter as SyncedAdapter).unsync(
                    viewHolder.adapterPosition
                )
            }
        }
        val itemTouchHelper = ItemTouchHelper(swiper)
        itemTouchHelper.attachToRecyclerView(syncedFanfictionsRecyclerView)
    }

    private fun setObservers() {
        viewModel.loadFanfictions()
        viewModel.getFanfictionList().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is SyncedFanfictionsResult.NoSyncedFanfictions -> {
                    fanfictionViewFlipper.displayedChild = DISPLAY_NO_SYNCED_FANFICTIONS
                }
                is SyncedFanfictionsResult.SyncedFanfictions -> {
                    (syncedFanfictionsRecyclerView.adapter as SyncedAdapter).fanfictionList = result.fanfictionList
                    fanfictionViewFlipper.displayedChild = DISPLAY_SYNCED_FANFICTIONS
                }
            }
        })
        viewModel.fanfictionRefreshResult.observe(viewLifecycleOwner, Observer { result ->
            swipeRefresh.isRefreshing = false
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        optionsController.onRequestPermissionsResult(requestCode, grantResults)
    }
}
