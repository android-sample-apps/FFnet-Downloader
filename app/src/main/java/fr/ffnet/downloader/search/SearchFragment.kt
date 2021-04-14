package fr.ffnet.downloader.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.common.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject


@FragmentScope
class SearchFragment : DaggerFragment(), HistoryAdapter.OnHistoryClickListener {

    private lateinit var viewModel: SearchViewModel
    @Inject lateinit var viewModelFactory: ViewModelFactory<SearchViewModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search, container, false).also {
        activity?.title = resources.getString(R.string.search_title)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        requireActivity().addOnBackPressedCallback {
            if (searchEditText.hasFocus()) {
                searchEditText.clearFocus()
                containerView.transitionToStart()
                true
            } else false
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                containerView.transitionToEnd()
            } else {
                containerView.transitionToStart()
            }
        }

        initRecyclerViews()
    }

    override fun onHistoryClicked(fanfictionId: String, fanfictionUrl: String) {

    }

    private fun initRecyclerViews() {

        mainListRecyclerView.adapter = HistoryAdapter(this)
        (mainListRecyclerView.adapter as HistoryAdapter).historyList = listOf(
            HistoryUIModel(
                fanfictionId = "92834232",
                url = "",
                title = "District 9",
                date = "14/04/2021"
            ),
            HistoryUIModel(
                fanfictionId = "92834232",
                url = "",
                title = "Paterson",
                date = "04/04/2021"
            ),
            HistoryUIModel(
                fanfictionId = "92834232",
                url = "",
                title = "Cherry",
                date = "20/04/2021"
            )
        )

        searchListRecyclerView.adapter = HistoryAdapter(this)
        (searchListRecyclerView.adapter as HistoryAdapter).historyList = listOf(
            HistoryUIModel(
                fanfictionId = "92834232",
                url = "",
                title = "Good morning, Vietnam",
                date = "20/04/2021"
            ),
            HistoryUIModel(
                fanfictionId = "92834232",
                url = "",
                title = "Leon",
                date = "21/04/2021"
            ),
            HistoryUIModel(
                fanfictionId = "92834232",
                url = "",
                title = "Chappie",
                date = "22/04/2021"
            )
        )
    }
}
