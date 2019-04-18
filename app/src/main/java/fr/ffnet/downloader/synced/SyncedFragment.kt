package fr.ffnet.downloader.synced

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.ViewModelFactory
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.fanfictionoptions.OptionsBottomSheetDialogFragment
import fr.ffnet.downloader.fanfictionoptions.OptionsBottomSheetDialogFragment.Companion.EXTRA_ACTION_DELETE
import fr.ffnet.downloader.fanfictionoptions.OptionsBottomSheetDialogFragment.Companion.EXTRA_ACTION_DETAILS
import fr.ffnet.downloader.fanfictionoptions.OptionsBottomSheetDialogFragment.Companion.EXTRA_ACTION_EPUB
import fr.ffnet.downloader.fanfictionoptions.OptionsBottomSheetDialogFragment.Companion.EXTRA_ACTION_PDF
import fr.ffnet.downloader.fanfictionoptions.OptionsBottomSheetDialogFragment.Companion.EXTRA_FANFICTION_ID
import fr.ffnet.downloader.synced.SyncedViewModel.SyncedFanfictionsResult
import fr.ffnet.downloader.utils.OnFanfictionOptionsListener
import kotlinx.android.synthetic.main.fragment_synced.*
import java.io.File
import javax.inject.Inject

class SyncedFragment : DaggerFragment(), OnFanfictionOptionsListener {

    private lateinit var fanfictionId: String
    private lateinit var viewModel: SyncedViewModel
    @Inject lateinit var viewModelFactory: ViewModelFactory<SyncedViewModel>

    companion object {
        private const val DISPLAY_SYNCED_FANFICTIONS = 0
        private const val DISPLAY_NO_SYNCED_FANFICTIONS = 1
        private const val STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val EXPORT_EPUB_REQUEST = 2000
        private const val EXPORT_PDF_REQUEST = 2001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = resources.getString(R.string.synced_title)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(
            SyncedViewModel::class.java
        )
        viewModel.loadFanfictionsFromDb()
        return inflater.inflate(R.layout.fragment_synced, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        viewModel.getFanfictionList().observe(this, Observer { fanfictionResult ->
            when (fanfictionResult) {
                is SyncedFanfictionsResult.NoSyncedFanfictions -> showNoSyncedFanfictions()
                is SyncedFanfictionsResult.SyncedFanfictions -> showSyncedFanfictions(
                    fanfictionResult.fanfictionList
                )
            }
        })
        viewModel.getFile.observe(this, Observer {
            it.getContentIfNotHandled()?.let { fileName ->
                val file = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    ), fileName
                )

                // Open file with user selected app
                val uri = Uri.fromFile(file).normalizeScheme()
                val mimeValue = getMimeType(uri.toString())
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = uri
                    type = mimeValue
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context?.startActivity(Intent.createChooser(intent, "Open file with"))
            }
        })
    }

    fun getMimeType(url: String): String? {
        val ext = MimeTypeMap.getFileExtensionFromUrl(url)
        var mime: String? = null
        if (ext != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        }
        return mime
    }

    override fun onOptionsClicked(
        fanfictionId: String,
        title: String,
        publishedDate: String,
        updatedDate: String,
        fetchedDate: String
    ) {
        val optionsFragment = OptionsBottomSheetDialogFragment.newInstance(
            fanfictionId,
            title,
            publishedDate,
            updatedDate,
            fetchedDate
        )
        optionsFragment.setTargetFragment(this, 1000)
        fragmentManager?.let {
            optionsFragment.show(it, "fanfiction_options")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                this.fanfictionId = intent.getStringExtra(EXTRA_FANFICTION_ID)
                when (intent.getStringExtra(
                    OptionsBottomSheetDialogFragment.EXTRA_ACTION
                )) {
                    EXTRA_ACTION_DETAILS -> startFanfictionActivity()
                    EXTRA_ACTION_PDF -> exportPdf()
                    EXTRA_ACTION_EPUB -> exportEpub()
                    EXTRA_ACTION_DELETE -> viewModel.unsyncFanfiction(fanfictionId)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                EXPORT_EPUB_REQUEST -> exportEpub()
                EXPORT_PDF_REQUEST -> exportPdf()
            }
        } else {
            AlertDialog
                    .Builder(context)
                    .setTitle(R.string.export_permission_title)
                    .setMessage(R.string.export_permission_content)
                    .setPositiveButton(R.string.export_permission_grant) { _, _ ->
                        checkPermission(requestCode)
                    }
                    .setNegativeButton(R.string.export_permission_deny) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }
    }

    private fun checkPermission(requestCode: Int): Boolean {
        return if (ActivityCompat.checkSelfPermission(context!!, STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(STORAGE), requestCode)
            false
        } else true
    }

    private fun exportEpub() {
        if (checkPermission(EXPORT_EPUB_REQUEST)) {
            viewModel.buildEpub(fanfictionId)
        }
    }

    private fun exportPdf() {
        if (checkPermission(EXPORT_PDF_REQUEST)) {
            viewModel.buildPdf(fanfictionId)
        }
    }

    private fun showSyncedFanfictions(fanfictionList: List<FanfictionSyncedUIModel>) {
        (syncedFanfictionsRecyclerView.adapter as SyncedAdapter).syncedList = fanfictionList
        fanfictionResultViewFlipper.displayedChild = DISPLAY_SYNCED_FANFICTIONS
    }

    private fun showNoSyncedFanfictions() {
        fanfictionResultViewFlipper.displayedChild = DISPLAY_NO_SYNCED_FANFICTIONS
    }

    private fun startFanfictionActivity() {
        context?.let { context ->
            startActivity(FanfictionActivity.intent(context, fanfictionId))
        }
    }

    private fun initRecyclerView() {
        syncedFanfictionsRecyclerView.layoutManager = LinearLayoutManager(context)
        syncedFanfictionsRecyclerView.adapter = SyncedAdapter(this)
    }
}
