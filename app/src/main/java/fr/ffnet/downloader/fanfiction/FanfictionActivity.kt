package fr.ffnet.downloader.fanfiction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import dagger.android.support.DaggerAppCompatActivity
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.notification.DownloadNotification
import kotlinx.android.synthetic.main.activity_fanfiction.*
import javax.inject.Inject

class FanfictionActivity : DaggerAppCompatActivity(), ChapterListAdapter.ChapterClickListener {

    @Inject lateinit var viewModel: FanfictionViewModel
    @Inject lateinit var notificationBuilder: DownloadNotification

    private val fanfictionId by lazy { intent.getStringExtra(EXTRA_ID) }

    companion object {

        private const val NOTIFICATION_CHANNEL_ID = "fanfictionDownloadProgress"
        private const val EXTRA_ID = "EXTRA_ID"

        fun intent(context: Context, id: String): Intent = Intent(
            context, FanfictionActivity::class.java
        ).apply {
            putExtra(EXTRA_ID, id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fanfiction)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        chapterListRecyclerView.adapter = ChapterListAdapter(this)

        viewModel.loadFanfictionInfo(fanfictionId)
        viewModel.loadChapters(fanfictionId)
        setListeners(fanfictionId)

        setObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onChapterSelected(chapter: ChapterUIModel) = Unit

    private fun setObservers() {
        viewModel.getFanfictionInfo().observe(this, Observer {
            widgetVisibilityGroup.visibility = View.VISIBLE
            titleValueTextView.text = it.title
            wordsValueTextView.text = it.words
            publishedDateValueTextView.text = it.publishedDate
            updatedDateValueTextView.text = it.updatedDate
            syncedDateValueTextView.text = it.syncedDate
            chaptersValueTextView.text = it.progressionText
        })
        viewModel.getChapterList().observe(this, Observer { chapterList ->
            (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = chapterList
        })
        viewModel.getDownloadButtonState().observe(this, Observer { (buttonText, shoudEnabled) ->
            downloadButton.text = buttonText
            downloadButton.isEnabled = shoudEnabled
        })
    }

    private fun setListeners(fanfictionId: String) {
        downloadButton.setOnClickListener {
            viewModel.syncChapters(fanfictionId)
        }
    }
}
