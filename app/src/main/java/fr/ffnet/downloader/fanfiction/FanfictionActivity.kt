package fr.ffnet.downloader.fanfiction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.activity_fanfiction.*
import javax.inject.Inject

class FanfictionActivity : DaggerAppCompatActivity(), ChapterListAdapter.ChapterClickListener {

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
            viewModel.loadFanfictionInfo(it)
            viewModel.loadChapters(it)
        } ?: closeActivityNoExtra()

        swipeRefresh.setOnRefreshListener {
            viewModel.refreshFanfictionInfo(intent.getStringExtra(EXTRA_ID))
        }
        downloadButton.setOnClickListener {
            viewModel.syncChapters(intent.getStringExtra(EXTRA_ID))
        }

        viewModel.stopRefresh.observe(this, Observer { liveEvent ->
            liveEvent.getContentIfNotHandled()?.let {
                swipeRefresh.isRefreshing = false
            }
        })
        viewModel.getFanfictionInfo().observe(this, Observer {
            swipeRefresh.isRefreshing = false
            widgetVisibilityGroup.visibility = View.VISIBLE
            titleValueTextView.text = it.title
            wordsValueTextView.text = it.words
            publishedDateValueTextView.text = it.publishedDate
            updatedDateValueTextView.text = it.updatedDate
            syncedDateValueTextView.text = it.syncedDate
        })
        viewModel.getChapterList().observe(this, Observer { chapterList ->
            (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = chapterList
        })
        viewModel.getChapterSyncingProgression().observe(this, Observer { chapterProgression ->
            chaptersValueTextView.text = chapterProgression
        })
    }

    override fun onChapterSelected(chapter: ChapterUIModel) {
        Toast.makeText(this, "Chapter selected ${chapter.title}", Toast.LENGTH_LONG).show()
    }

    private fun closeActivityNoExtra() {
        Toast.makeText(this, "No fanfiction found, closing", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun initRecyclerView() {
        chapterListRecyclerView.layoutManager = LinearLayoutManager(this)
        chapterListRecyclerView.adapter = ChapterListAdapter(this)
    }

}
