package fr.ffnet.downloader.fanfiction

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.fanfiction.ChapterListAdapter.ChapterClickListener
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.SyncState
import fr.ffnet.downloader.fanfiction.injection.FanfictionModule
import fr.ffnet.downloader.synced.FanfictionUIItem.FanfictionUI
import fr.ffnet.downloader.synced.OptionsController
import fr.ffnet.downloader.synced.PermissionListener
import kotlinx.android.synthetic.main.activity_fanfiction.*
import javax.inject.Inject

class FanfictionActivity : AppCompatActivity(), ChapterClickListener, PermissionListener, SyncingFinishedListener {

    @Inject lateinit var viewModel: FanfictionViewModel
    @Inject lateinit var optionsController: OptionsController

    private val fanfictionId by lazy { intent.getStringExtra(EXTRA_ID) ?: "" }

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        optionsController.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onChapterSelected(chapter: ChapterUIModel) = Unit

    override fun onExportPdf() {
        optionsController.onExportPdf(fanfictionId)
    }

    override fun onExportEpub() {
        optionsController.onExportEpub(fanfictionId)
    }

    private fun setObservers() {
        viewModel.getFanfictionInfo().observe(this, { fanfiction ->
            onFanfictionInfo(fanfiction)
        })
        viewModel.getChapterList().observe(this, { chapterList ->
            (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = chapterList
        })
        viewModel.getDownloadButtonState().observe(this, { buttonState ->
            val shouldEnable = buttonState is SyncState.Sync
            FFLogger.d(
                FFLogger.EVENT_KEY,
                "Changing download button state for $fanfictionId to $shouldEnable"
            )
            if (downloadButton.isEnabled.not() && shouldEnable) {
                val syncingFinishedFragment = SyncingFinishedFragment.newIntent()
                syncingFinishedFragment.show(supportFragmentManager, "syncingFinished")
            }
            downloadButton.text = buttonState.buttonTitle
            downloadButton.isEnabled = shouldEnable
        })
    }

    private fun onFanfictionInfo(fanfiction: FanfictionUI) {
        widgetVisibilityGroup.visibility = View.VISIBLE
        toolbar.title = fanfiction.title
        wordsValueTextView.text = fanfiction.words
        publishedDateValueTextView.text = fanfiction.publishedDate
        updatedDateValueTextView.text = fanfiction.updatedDate
        syncedDateValueTextView.text = fanfiction.fetchedDate
        chaptersValueTextView.text = fanfiction.progressionText

        exportPdfImageView.setBackgroundResource(fanfiction.exportPdfImage)
        exportEpubImageView.setBackgroundResource(fanfiction.exportEpubImage)
        unsyncImageView.setBackgroundResource(
            if (fanfiction.isDownloadComplete) R.drawable.ic_trash_enabled else R.drawable.ic_trash_disabled
        )

        if (fanfiction.isDownloadComplete) {
            exportPdfImageView.setOnClickListener {
                optionsController.onExportPdf(fanfiction.id)
            }
            exportEpubImageView.setOnClickListener {
                optionsController.onExportEpub(fanfiction.id)
            }
            unsyncImageView.setOnClickListener {
                optionsController.onUnsync(fanfiction)
                finish()
            }
        }
    }

    private fun setListeners(fanfictionId: String) {
        downloadButton.setOnClickListener {
            viewModel.syncChapters(fanfictionId)
        }
    }

    override fun onPermissionRequested(arrayOf: Array<String>, requestCode: Int): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                OptionsController.STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        ) {
            requestPermissions(arrayOf(OptionsController.STORAGE), requestCode)
            false
        } else true
    }
}
