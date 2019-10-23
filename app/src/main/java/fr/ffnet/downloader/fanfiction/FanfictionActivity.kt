package fr.ffnet.downloader.fanfiction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.ViewModelFactory
import fr.ffnet.downloader.repository.DownloaderWorkerFactory
import kotlinx.android.synthetic.main.activity_fanfiction.*
import javax.inject.Inject

class FanfictionActivity : DaggerAppCompatActivity(), ChapterListAdapter.ChapterClickListener {

    private lateinit var viewModel: FanfictionViewModel
    @Inject lateinit var viewModelFactory: ViewModelFactory<FanfictionViewModel>
//    @Inject lateinit var workerFactory: DownloaderWorkerFactory

    companion object {

        private const val EXTRA_ID = "EXTRA_ID"

        fun intent(context: Context, id: String): Intent = Intent(
            context, FanfictionActivity::class.java
        ).apply {
            putExtra(EXTRA_ID, id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(
            FanfictionViewModel::class.java
        )

//        val configuration = Configuration.Builder().setWorkerFactory(workerFactory).build()
//        WorkManager.initialize(this, configuration)

        setContentView(R.layout.activity_fanfiction)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initRecyclerView()

        intent.getStringExtra(EXTRA_ID)?.let { fanfictionId ->
            viewModel.loadFanfictionInfo(fanfictionId)
            viewModel.loadChapters(fanfictionId)
//            viewModel.loadChapterDownloadingState()
            setListeners(fanfictionId)
        } ?: closeActivityNoExtra()

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
            chaptersValueTextView.text = it.progression
        })
        viewModel.getChapterList().observe(this, Observer { chapterList ->
            (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = chapterList
        })
//        viewModel.getDownloadButtonState().observe(this, Observer { chapterDownloadResult ->
//            when (chapterDownloadResult) {
//                FanfictionViewModel.ChapterStatusState.ChapterSynced -> downloadButton.isEnabled = true
//                FanfictionViewModel.ChapterStatusState.ChapterSyncing -> downloadButton.isEnabled = false
//                is FanfictionViewModel.ChapterStatusState.ChapterSyncError -> enableDownloadButtonAndDisplayErrorMessage(
//                    chapterDownloadResult.message
//                )
//                is FanfictionViewModel.ChapterStatusState.ChaptersAlreadySynced -> enableDownloadButtonAndDisplayErrorMessage(
//                    chapterDownloadResult.message
//                )
//            }
//        })
        viewModel.getDownloadButtonState().observe(this, Observer {
            println("WorkManager state -> $it")
        })
    }

    private fun enableDownloadButtonAndDisplayErrorMessage(message: String) {
        downloadButton.isEnabled = true
        Snackbar.make(containerConstraintLayout, message, Snackbar.LENGTH_LONG).show()
    }

    private fun setListeners(fanfictionId: String) {
        downloadButton.setOnClickListener {
            viewModel.syncChapters(fanfictionId)
        }
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
