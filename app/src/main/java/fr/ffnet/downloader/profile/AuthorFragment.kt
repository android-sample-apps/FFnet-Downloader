package fr.ffnet.downloader.profile

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
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import kotlinx.android.synthetic.main.fragment_author.*
import javax.inject.Inject

class AuthorFragment : Fragment(), OnAuthorListener {

    @Inject lateinit var viewModel: AuthorViewModel

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
        viewModel.loadAuthor(authorId)
    }

    private fun initializeSearch() {
        syncedFanfictionsRecyclerView.adapter = AuthorListAdapter(this)

        viewModel.loadSearchAndSynced()
        viewModel.authorResult.observe(viewLifecycleOwner, { authorItemList ->
            (syncedFanfictionsRecyclerView.adapter as AuthorListAdapter).authorItemList = authorItemList
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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val search = searchEditText.text.toString().isBlank()
                    when {
                        searchEditText.hasFocus() && search -> transitionToStart()
                        searchEditText.hasFocus() -> searchEditText.clearFocus()
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
