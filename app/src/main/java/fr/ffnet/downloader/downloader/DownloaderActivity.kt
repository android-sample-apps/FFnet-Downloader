package fr.ffnet.downloader.downloader

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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

        downloadButton.setOnClickListener {
            viewModel.loadFanfictionInfos(downloadUrlEditText.text.toString())
        }
        initRecyclerView()

        viewModel.getCurrentFanfiction().observe(this, Observer {
            titleValueTextView.text = it.title
            wordsValueTextView.text = it.words
            (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = it.chapterList

            viewModel.getChapterList().observe(this, Observer { chapterList ->
                (chapterListRecyclerView.adapter as ChapterListAdapter).chapterList = chapterList
            })
        })
    }

    override fun onChapterSelected(chapter: ChapterViewModel) {
        Toast.makeText(this, "Chapter selected ${chapter.title}", Toast.LENGTH_LONG).show()
    }

    private fun initRecyclerView() {
        chapterListRecyclerView.layoutManager = LinearLayoutManager(this)
        chapterListRecyclerView.adapter = ChapterListAdapter(this)
    }
}
