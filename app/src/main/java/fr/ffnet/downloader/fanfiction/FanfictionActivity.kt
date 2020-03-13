package fr.ffnet.downloader.fanfiction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.fanfiction.injection.FanfictionModule
import fr.ffnet.downloader.fanfictionutils.FanfictionOpener
import kotlinx.android.synthetic.main.activity_fanfiction.*
import javax.inject.Inject

class FanfictionActivity : AppCompatActivity(), ChapterListAdapter.ChapterClickListener {

    @Inject lateinit var viewModel: FanfictionViewModel

    private lateinit var fanfictionOpener: FanfictionOpener

    private val fanfictionId by lazy { intent.getStringExtra(EXTRA_ID) }

    companion object {

        private const val EXTRA_ID = "EXTRA_ID"

        fun intent(context: Context, fanfictionId: String): Intent = Intent(
            context, FanfictionActivity::class.java
        ).apply {
            putExtra(EXTRA_ID, fanfictionId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainApplication.getComponent(this)
            .plus(FanfictionModule(this))
            .inject(this)

        setContentView(R.layout.activity_fanfiction)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        chapterListRecyclerView.adapter = ChapterListAdapter(this)

        fanfictionOpener = FanfictionOpener(this)
        viewModel.loadFanfictionInfo(fanfictionId)
        viewModel.loadChapters(fanfictionId)
        setListeners(fanfictionId)

        setObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.fanfiction_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.exportEpub -> {
                true
            }
            R.id.exportPdf -> {
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
