package fr.ffnet.downloader.search

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.search.injection.SearchModule
import fr.ffnet.downloader.synced.FanfictionListAdapter
import fr.ffnet.downloader.synced.OnHistoryClickListener
import fr.ffnet.downloader.synced.OnSyncAllFanfictionsListener
import fr.ffnet.downloader.synced.OptionsController
import fr.ffnet.downloader.synced.ParentListener
import fr.ffnet.downloader.synced.SyncedViewModel
import fr.ffnet.downloader.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

class SearchFragment :
    Fragment(),
    OnHistoryClickListener,
    ParentListener,
    OnSyncAllFanfictionsListener {

    @Inject lateinit var searchViewModel: SearchViewModel

    @Inject lateinit var syncedViewModel: SyncedViewModel
    @Inject lateinit var optionsController: OptionsController

    companion object {
        private const val DISPLAY_SYNCED_FANFICTIONS = 0
        private const val DISPLAY_NO_SYNCED_FANFICTIONS = 1
    }

    enum class KeyboardStatus {
        CLOSED, OPENED
    }

    private var keyboardStatus = KeyboardStatus.CLOSED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        view?.let(::setKeyboardStatusListener)
        return view
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
        transitionToStart()
        optionsController.onFetchInformation(fanfictionId)
    }

    private fun initializeSynced() {
        syncedFanfictionsRecyclerView.adapter = FanfictionListAdapter(
            onActionListener = optionsController,
            syncAllListener = this
        )
        val itemTouchHelper = ItemTouchHelper(object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (syncedFanfictionsRecyclerView.adapter as FanfictionListAdapter).unsync(
                    viewHolder.bindingAdapterPosition
                )
            }
        })
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

    override fun onSyncAll() {
        syncedViewModel.syncAllUnsyncedChapters()
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(containerView, message, Snackbar.LENGTH_LONG).show()
    }

    private fun setKeyboardStatusListener(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                if (keyboardStatus == KeyboardStatus.CLOSED) {
                    keyboardStatus = KeyboardStatus.OPENED
                }
            } else {
                if (keyboardStatus == KeyboardStatus.OPENED) {
                    keyboardStatus = KeyboardStatus.CLOSED
                    searchEditText.clearFocus()
                }
            }
        }
    }

    private fun initializeSearch() {

        searchViewModel.loadSearchAndHistory()
        searchResultRecyclerView.adapter = FanfictionListAdapter(
            onActionListener = optionsController,
            syncAllListener = this,
            historyListener = this
        )

        searchEditText.addTextChangedListener {
            searchViewModel.emptySearchResult()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchEditText.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.square_corners
                )
                containerView.transitionToEnd()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val search = searchEditText.text.toString().isBlank()
                    when {
                        searchEditText.hasFocus() && search -> transitionToStart()
                        searchEditText.hasFocus() -> searchEditText.clearFocus()
                        searchResultRecyclerView.isVisible -> transitionToStart()
                        else -> requireActivity().finish()
                    }
                }
            }
        )


        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view?.windowToken, 0)
                searchEditText.clearFocus()
                searchViewModel.searchFanfiction(searchEditText.text.toString())
            }
            true
        }
    }

    private fun transitionToStart() {
        searchEditText.clearFocus()
        searchEditText.setText("")
        searchEditText.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.round_corners
        )
        containerView.transitionToStart()
    }

    private fun setSyncedObservers() {
        syncedViewModel.loadSyncedFanfictions().observe(viewLifecycleOwner, { result ->
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
            optionsController.onFetchInformation(fanfictionId)
            transitionToStart()
        })
        searchViewModel.searchHistoryResult.observe(viewLifecycleOwner, { historyList ->
            (searchResultRecyclerView.adapter as FanfictionListAdapter).fanfictionItemList = historyList
        })
    }
}
