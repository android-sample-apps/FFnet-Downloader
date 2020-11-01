package fr.ffnet.downloader.search

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.search.injection.SearchModule
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

class SearchFragment : Fragment(), HistoryAdapter.OnHistoryClickListener {

    @Inject lateinit var viewModel: SearchViewModel

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

        viewModel.schedulePeriodicJob()

        fetchInformationButton.setOnClickListener {
            it.isEnabled = false
            progressBar.visibility = View.VISIBLE

            (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)

            viewModel.loadFanfictionInfos(downloadUrlEditText.text.toString())
        }
        historyRecyclerView.adapter = HistoryAdapter(this)
        initObservers()
    }

    override fun onHistoryClicked(fanfictionId: String, fanfictionUrl: String) {
        downloadUrlEditText.setText(fanfictionUrl)
    }

    private fun initObservers() {
        viewModel.navigateToFanfiction.observe(viewLifecycleOwner, Observer { fanfictionId ->
            startActivity(FanfictionActivity.intent(requireContext(), fanfictionId)).also {
                fetchInformationButton.isEnabled = true
                progressBar.visibility = View.GONE
            }
        })
        viewModel.loadHistory().observe(viewLifecycleOwner, Observer { historyList ->
            (historyRecyclerView.adapter as HistoryAdapter).historyList = historyList
        })
        viewModel.sendError.observe(viewLifecycleOwner, Observer { searchError ->
            when (searchError) {
                is SearchViewModel.SearchError.UrlNotValid,
                is SearchViewModel.SearchError.InfoFetchingFailed -> {
                    Snackbar.make(
                        containerView, searchError.message, Snackbar.LENGTH_LONG
                    ).show()
                    fetchInformationButton.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            }
        })
    }
}
