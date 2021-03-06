package fr.ffnet.downloader.profile.injection

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.profile.AuthorListAdapter
import fr.ffnet.downloader.profile.AuthorModule
import fr.ffnet.downloader.profile.AuthorUIItem.SyncedAuthorUIItem
import fr.ffnet.downloader.profile.AuthorViewModel
import fr.ffnet.downloader.profile.AuthorViewModel.AuthorRefreshResult
import fr.ffnet.downloader.profile.OnAuthorListener
import fr.ffnet.downloader.profile.fanfiction.AuthorDetailActivity
import fr.ffnet.downloader.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_author.*
import kotlinx.android.synthetic.main.fragment_author.containerView
import kotlinx.android.synthetic.main.fragment_author.searchEditText
import kotlinx.android.synthetic.main.fragment_author.swipeRefresh
import kotlinx.android.synthetic.main.fragment_author.syncedFanfictionsRecyclerView
import javax.inject.Inject

class AuthorFragment : Fragment(), OnAuthorListener {

    @Inject lateinit var viewModel: AuthorViewModel

    companion object {
        private const val DISPLAY_AUTHOR_CONTENT = 0
        private const val DISPLAY_NO_AUTHOR_CONTENT = 1
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
        val view = inflater.inflate(R.layout.fragment_author, container, false)
        view?.let(::setKeyboardStatusListener)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(AuthorModule(this))
            .inject(this)

        initializeSearch()
    }

    override fun onLoadAuthor(authorId: String) {
        viewModel.loadAuthorInfo(authorId)
    }

    override fun onUnsync(author: SyncedAuthorUIItem) {
        viewModel.unsyncAuthor(author)
    }

    private fun initializeSearch() {

        syncedFanfictionsRecyclerView.adapter = AuthorListAdapter(this)

        swipeRefresh.setOnRefreshListener {
            viewModel.refreshAuthorsInfo()
        }

        viewModel.authorRefreshResult.observe(viewLifecycleOwner, {
            swipeRefresh.isRefreshing = false
            if (it is AuthorRefreshResult.NotRefreshed) {
                Snackbar.make(containerView, it.message, Snackbar.LENGTH_LONG).show()
            }
        })

        viewModel.navigateToAuthor.observe(viewLifecycleOwner, { authorLoaded ->
            transitionToStart()
            startActivity(
                AuthorDetailActivity.newIntent(
                    context = requireContext(),
                    authorId = authorLoaded.authorId,
                    authorName = authorLoaded.authorName,
                    shouldShowStoriesFirst = authorLoaded.shouldShowStoriesFirst
                )
            )
        })
        viewModel.error.observe(viewLifecycleOwner, { errorMessage ->
            Snackbar.make(containerView, errorMessage, Snackbar.LENGTH_LONG).show()
        })
        viewModel.loadSearchAndSynced()
        viewModel.authorResult.observe(viewLifecycleOwner, { authorItemList ->
            if (authorItemList.isNotEmpty()) {
                (syncedFanfictionsRecyclerView.adapter as AuthorListAdapter).authorItemList = authorItemList
                syncedViewFlipper.displayedChild = DISPLAY_AUTHOR_CONTENT
            } else {
                syncedViewFlipper.displayedChild = DISPLAY_NO_AUTHOR_CONTENT
            }
        })

        searchEditText.addTextChangedListener {
            viewModel.emptySearchResults()
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

        val itemTouchHelper = ItemTouchHelper(object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (syncedFanfictionsRecyclerView.adapter as AuthorListAdapter).unsync(
                    viewHolder.bindingAdapterPosition
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(syncedFanfictionsRecyclerView)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val search = searchEditText.text.toString()
                    when {
                        searchEditText.hasFocus() && search.isBlank() -> transitionToStart()
                        searchEditText.hasFocus() -> searchEditText.clearFocus()
                        search.isNotBlank() -> transitionToStart()
                        else -> {
                            val navigationView: BottomNavigationView = requireActivity().findViewById(
                                R.id.navigationView
                            )
                            navigationView.selectedItemId = R.id.bottomNavSearchFragment
                        }
                    }
                }
            }
        )

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view?.windowToken, 0)
                searchEditText.clearFocus()
                viewModel.searchAuthor(searchEditText.text.toString())
            }
            true
        }
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

    private fun transitionToStart() {
        searchEditText.clearFocus()
        searchEditText.setText("")
        searchEditText.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.round_corners
        )
        containerView.transitionToStart()
    }
}
