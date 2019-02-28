package fr.ffnet.downloader.search

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.common.ViewModelFactory
import kotlinx.android.synthetic.main.activity_downloader.*
import javax.inject.Inject

class DownloaderActivity : AppCompatActivity(), ChapterListAdapter.ChapterClickListener {

    @Inject lateinit var factory: ViewModelFactory<DownloaderViewModel>
    @Inject lateinit var dao: FanfictionDao
    private lateinit var viewModel: DownloaderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_downloader)

        MainApplication.getComponent(this).plus(DownloaderModule()).inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(
            DownloaderViewModel::class.java
        )

//        fetchInformationButton.setOnClickListener {
//            widgetVisibilityGroup.visibility = View.GONE
//            viewModel.loadFanfictionInfos(downloadUrlEditText.text.toString())
//        }
//
//        downloadButton.setOnClickListener {
//            viewModel.loadChapters()
//            viewModel.getChapterList().observe(this, Observer { chapterList ->
//                (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = chapterList
//            })
//        }
//        initRecyclerView()
//
//        viewModel.getCurrentFanfiction().observe(this, Observer {
//            widgetVisibilityGroup.visibility = View.VISIBLE
//            titleValueTextView.text = it.title
//            wordsValueTextView.text = it.words
//            publishedDateValueTextView.text = it.publishedDate
//            updatedDateValueTextView.text = it.updatedDate
//            (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = it.chapterList
//
//            viewModel.getChapterSyncingProgression().observe(this, Observer { chapterProgression ->
//                chaptersValueTextView.text = chapterProgression
//            })
//        })r
    }

    override fun onChapterSelected(chapter: ChapterViewModel) {
        Toast.makeText(this, "Chapter selected ${chapter.title}", Toast.LENGTH_LONG).show()
    }

    private fun initRecyclerView() {
        chapterListRecyclerView.layoutManager = LinearLayoutManager(this)
        chapterListRecyclerView.adapter = ChapterListAdapter(this)
    }
}
