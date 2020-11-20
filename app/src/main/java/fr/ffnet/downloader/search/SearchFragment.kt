package fr.ffnet.downloader.search

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.search.injection.SearchModule
import fr.ffnet.downloader.synced.FanfictionListAdapter
import fr.ffnet.downloader.synced.OnSyncAllFanfictionsListener
import fr.ffnet.downloader.synced.OptionsController
import fr.ffnet.downloader.synced.PermissionListener
import fr.ffnet.downloader.synced.SyncedViewModel
import fr.ffnet.downloader.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

class SearchFragment :
    Fragment(),
    HistoryAdapter.OnHistoryClickListener,
    PermissionListener,
    OnSyncAllFanfictionsListener {

    @Inject lateinit var searchViewModel: SearchViewModel

    @Inject lateinit var syncedViewModel: SyncedViewModel
    @Inject lateinit var optionsController: OptionsController

    companion object {
        private const val DISPLAY_SYNCED_FANFICTIONS = 0
        private const val DISPLAY_NO_SYNCED_FANFICTIONS = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search, container, false).also {
        requireActivity().title = resources.getString(R.string.search_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(SearchModule(this))
            .inject(this)

        initializeSearch()
        setSearchObservers()

        initializeSynced()
        setSyncedObservers()
    }

    override fun onHistoryClicked(fanfictionId: String, fanfictionUrl: String) {
        downloadUrlEditText.setText(fanfictionUrl)
    }

    private fun initializeSynced() {
        syncedFanfictionsRecyclerView.adapter = FanfictionListAdapter(optionsController, this)
        val swiper = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (syncedFanfictionsRecyclerView.adapter as FanfictionListAdapter).unsync(
                    viewHolder.adapterPosition
                )
            }
        }
        val itemTouchHelper = ItemTouchHelper(swiper)
        itemTouchHelper.attachToRecyclerView(syncedFanfictionsRecyclerView)
        swipeRefresh.setOnRefreshListener {
            syncedViewModel.refreshSyncedInfo()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        optionsController.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onPermissionRequested(arrayOf: Array<String>, requestCode: Int): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireContext(),
                OptionsController.STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        ) {
            requestPermissions(arrayOf(OptionsController.STORAGE), requestCode)
            false
        } else true
    }

    private fun initializeSearch() {

        searchResultRecyclerView.adapter = HistoryAdapter(this)

        downloadUrlEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                downloadUrlEditText.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.square_corners
                )
                containerView.transitionToEnd()
            } else {
                downloadUrlEditText.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_corners
                )
                containerView.transitionToStart()
            }
        }

        downloadUrlEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (downloadUrlEditText.hasFocus()) {
                        downloadUrlEditText.clearFocus()
                    } else {
                        requireActivity().finish()
                    }
                }
            }
        )

        downloadUrlEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view?.windowToken, 0)

                searchViewModel.loadFanfictionInfos(downloadUrlEditText.text.toString())
            }
            true
        }
    }

    private fun setSyncedObservers() {
        syncedViewModel.loadFanfictions()
        syncedViewModel.getFanfictionList().observe(viewLifecycleOwner, { result ->
            when (result) {
                is SyncedViewModel.SyncedFanfictionsResult.NoSyncedFanfictions -> {
                    syncedViewFlipper.displayedChild = DISPLAY_NO_SYNCED_FANFICTIONS
                }
                is SyncedViewModel.SyncedFanfictionsResult.SyncedFanfictions -> {
                    (syncedFanfictionsRecyclerView.adapter as FanfictionListAdapter).fanfictionItemList = result.fanfictionUIItemList
                    syncedViewFlipper.displayedChild = DISPLAY_SYNCED_FANFICTIONS
                }
            }
        })
        syncedViewModel.fanfictionRefreshResult.observe(viewLifecycleOwner, {
            swipeRefresh.isRefreshing = false
        })
    }

    private fun setSearchObservers() {
        searchViewModel.navigateToFanfiction.observe(viewLifecycleOwner, { fanfictionId ->
            containerView.transitionToStart()
            startActivity(FanfictionActivity.intent(requireContext(), fanfictionId))
        })
        searchViewModel.loadHistory().observe(viewLifecycleOwner, { historyList ->
            (searchResultRecyclerView.adapter as HistoryAdapter).historyList = historyList
        })
        searchViewModel.sendError.observe(viewLifecycleOwner, { searchError ->
            when (searchError) {
                is SearchViewModel.SearchError.UrlNotValid,
                is SearchViewModel.SearchError.InfoFetchingFailed -> {
                    Snackbar.make(
                        containerView, searchError.message, Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    override fun onSyncAll() {
        syncedViewModel.syncAllUnsyncedChapters()
    }
}
