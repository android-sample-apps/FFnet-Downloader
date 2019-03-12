package fr.ffnet.downloader.synced

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.ViewModelFactory
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.synced.SyncedViewModel.SyncedFanfictionsResult
import fr.ffnet.downloader.utils.FanfictionAction
import fr.ffnet.downloader.utils.OnActionsClickListener
import kotlinx.android.synthetic.main.fragment_synced.*
import nl.siegmann.epublib.domain.Author
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubWriter
import java.io.FileOutputStream
import javax.inject.Inject

class SyncedFragment : DaggerFragment(), OnActionsClickListener {

    private lateinit var viewModel: SyncedViewModel
    @Inject lateinit var viewModelFactory: ViewModelFactory<SyncedViewModel>

    companion object {
        private const val DISPLAY_SYNCED_FANFICTIONS = 0
        private const val DISPLAY_NO_SYNCED_FANFICTIONS = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_synced, container, false).also {
            activity?.title = resources.getString(R.string.synced_title)
            viewModel = ViewModelProviders.of(this, viewModelFactory).get(
                SyncedViewModel::class.java
            )
            viewModel.loadFanfictionsFromDb()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

//        activity?.requestPermissions(context, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))


        viewModel.getFanfictionList().observe(this, Observer { fanfictionResult ->
            when (fanfictionResult) {
                is SyncedFanfictionsResult.NoSyncedFanfictions -> showNoSyncedFanfictions()
                is SyncedFanfictionsResult.SyncedFanfictions -> showSyncedFanfictions(
                    fanfictionResult.fanfictionList
                )
            }
        })
    }

    override fun onActionClicked(fanfictionId: String, action: FanfictionAction) {
        when (action) {
            FanfictionAction.GOTO_FANFICTION -> startFanfictionActivity(fanfictionId)
            FanfictionAction.EXPORT_PDF -> Log.d("ACTION", "EXPORT_PDF")
            FanfictionAction.EXPORT_EPUB -> exportPdf(fanfictionId)
            FanfictionAction.DELETE_FANFICTION -> {
                viewModel.deleteFanfiction(fanfictionId)
            }
        }
    }

    private fun exportPdf(fanfictionId: String) {

        val book = Book()
        book.metadata.addTitle("A title")
        book.metadata.addAuthor(Author("An author"))

        val epubWriter = EpubWriter()
        epubWriter.write(book, FileOutputStream("test_epub.epub"))


    }

    private fun showSyncedFanfictions(fanfictionList: List<FanfictionSyncedUIModel>) {
        (syncedFanfictionsRecyclerView.adapter as SyncedAdapter).syncedList = fanfictionList
        fanfictionResultViewFlipper.displayedChild = DISPLAY_SYNCED_FANFICTIONS
    }

    private fun showNoSyncedFanfictions() {
        fanfictionResultViewFlipper.displayedChild = DISPLAY_NO_SYNCED_FANFICTIONS
    }

    private fun startFanfictionActivity(fanfictionId: String) {
        context?.let { context ->
            startActivity(FanfictionActivity.intent(context, fanfictionId))
        }
    }

    private fun initRecyclerView() {
        syncedFanfictionsRecyclerView.layoutManager = LinearLayoutManager(context)
        syncedFanfictionsRecyclerView.adapter = SyncedAdapter(this)
    }
}