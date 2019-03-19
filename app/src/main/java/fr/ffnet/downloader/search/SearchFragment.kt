package fr.ffnet.downloader.search

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.ViewModelFactory
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

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

        fetchInformationButton.setOnClickListener {
            it.isEnabled = false
            progressBar.visibility = View.VISIBLE

            (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)

            viewModel.loadFanfictionInfos(downloadUrlEditText.text.toString())
        }
        initRecyclerView()
        setErrorListener()

        viewModel.navigateToFanfiction.observe(this, Observer { liveEvent ->
            liveEvent.getContentIfNotHandled()?.let {
                if (context != null) {
                    startActivity(FanfictionActivity.intent(context!!, it)).also {
                        fetchInformationButton.isEnabled = true
                        progressBar.visibility = View.GONE
                    }
                }
            }
        })

        viewModel.loadHistory().observe(this, Observer { historyList ->
            (historyRecyclerView.adapter as HistoryAdapter).historyList = historyList
        })
    }

    private fun setErrorListener() {
        viewModel.sendError.observe(this, Observer { liveEvent ->
            liveEvent.getContentIfNotHandled()?.let { searchError ->
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
            }
        })
    }

    override fun onHistoryClicked(fanfictionId: String, fanfictionUrl: String) {
        downloadUrlEditText.setText(fanfictionUrl)
    }

    private fun initRecyclerView() {
        historyRecyclerView.layoutManager = LinearLayoutManager(context)
        historyRecyclerView.adapter = HistoryAdapter(this)
    }
}
