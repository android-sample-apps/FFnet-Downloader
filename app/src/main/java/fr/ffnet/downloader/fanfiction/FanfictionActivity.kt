package fr.ffnet.downloader.fanfiction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.activity_fanfiction.*
import javax.inject.Inject

class FanfictionActivity : DaggerAppCompatActivity(), FanfictionInfoAdapter.OnSyncListener {

    @Inject lateinit var viewModel: FanfictionViewModel

    companion object {

        private const val EXTRA_ID = "extraId"

        fun intent(context: Context, id: String): Intent = Intent(
            context, FanfictionActivity::class.java
        ).apply {
            putExtra(EXTRA_ID, id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fanfiction)
        initRecyclerView()

        intent.getStringExtra(EXTRA_ID)?.let {
            viewModel.loadInfo(it)
        } ?: closeActivityNoExtra()

        swipeRefresh.setOnRefreshListener {
            viewModel.refreshFanfictionInfo(intent.getStringExtra(EXTRA_ID))
        }

        viewModel.getFanfictionInfo().observe(this, Observer {
            swipeRefresh.isRefreshing = false
            (chapterListRecyclerView.adapter as FanfictionInfoAdapter).fanfictionInfoList = it
        })
    }

    override fun onSyncClicked(fanfictionId: String) {
        viewModel.syncChapters(fanfictionId)
    }

    private fun closeActivityNoExtra() {
        Toast.makeText(this, "No fanfiction found, closing", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun initRecyclerView() {
        chapterListRecyclerView.layoutManager = LinearLayoutManager(this)
        chapterListRecyclerView.adapter = FanfictionInfoAdapter(this)
    }
}
