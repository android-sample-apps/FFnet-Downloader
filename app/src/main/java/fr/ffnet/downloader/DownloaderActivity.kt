package fr.ffnet.downloader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.common.ViewModelFactory
import kotlinx.android.synthetic.main.activity_downloader.*
import javax.inject.Inject

class DownloaderActivity : AppCompatActivity() {

    @Inject lateinit var factory: ViewModelFactory<DownloaderViewModel>
    private lateinit var viewModel: DownloaderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_downloader)

        MainApplication.getComponent(this).plus(DownloaderModule()).inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(DownloaderViewModel::class.java)


        downloadButton.setOnClickListener {
            viewModel.loadFanfictionInfos(downloadUrlEditText.text.toString())
        }

        viewModel.currentFanfiction.observe(this, Observer {
            println(it.chapterList.size)
        })
    }
}
