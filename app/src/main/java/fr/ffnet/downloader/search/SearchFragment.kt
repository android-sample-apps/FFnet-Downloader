package fr.ffnet.downloader.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FragmentScope
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

@FragmentScope
class SearchFragment : DaggerFragment(), HistoryAdapter.OnHistoryClickListener {

    @Inject lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false).also {
            activity?.title = resources.getString(R.string.search_fanfiction_title)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchInformationButton.setOnClickListener {
            viewModel.loadFanfictionInfos(downloadUrlEditText.text.toString())
        }

        viewModel.navigateToFanfiction.observe(this, Observer { liveEvent ->
            liveEvent.getContentIfNotHandled()?.let {
                if (context != null) {
                    startActivity(FanfictionActivity.intent(context!!, it))
                }
            }
        })

        initRecyclerView()
        viewModel.loadHistory().observe(this, Observer { historyList ->
            (historyRecyclerView.adapter as HistoryAdapter).historyList = historyList
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
