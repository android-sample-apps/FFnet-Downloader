package fr.ffnet.downloader.synced

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.fanfictionoptions.OptionsViewModel
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.OnFanfictionActionsListener

class OptionsController(
    private val fragment: Fragment,
    private val optionsViewModel: OptionsViewModel,
    private val fanfictionOpener: FanfictionOpener
) : OnFanfictionActionsListener {

    companion object {
        private const val STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val EXPORT_EPUB_REQUEST = 2000
        private const val EXPORT_PDF_REQUEST = 2001
    }

    private val absolutePath: String by lazy {
        fragment.requireContext().getExternalFilesDir(
            Environment.DIRECTORY_DOCUMENTS
        )?.absolutePath ?: throw IllegalArgumentException()
    }
    private var exportFanfictionId: String = ""

    init {

        optionsViewModel.getFile.observe(
            fragment.viewLifecycleOwner,
            Observer { (fileName, absolutePath) ->
                fanfictionOpener.openFile(fileName, absolutePath)
            })
        optionsViewModel.navigateToFanfiction.observe(
            fragment.viewLifecycleOwner,
            Observer { fanfictionId ->
                fragment.startActivity(
                    FanfictionActivity.intent(
                        fragment.requireContext(),
                        fanfictionId
                    )
                )
            })
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                EXPORT_EPUB_REQUEST -> exportEpub()
                EXPORT_PDF_REQUEST -> exportPdf()
            }
        } else {
            AlertDialog
                .Builder(fragment.requireContext())
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

    override fun onFetchInformation(fanfiction: FanfictionSyncedUIModel) {
        FFLogger.d(FFLogger.EVENT_KEY, "Opening details for ${fanfiction.id}")
        optionsViewModel.loadFanfictionInfo(fanfiction.id)
    }

    override fun onExportPdf(fanfiction: FanfictionSyncedUIModel) {
        exportFanfictionId = fanfiction.id
        exportPdf()
    }

    override fun onExportEpub(fanfiction: FanfictionSyncedUIModel) {
        exportFanfictionId = fanfiction.id
        exportEpub()
    }

    override fun onUnsync(fanfiction: FanfictionSyncedUIModel) {
        FFLogger.d(FFLogger.EVENT_KEY, "Unsync ${fanfiction.id}")
        optionsViewModel.unsyncFanfiction(fanfiction.id)
    }

    private fun checkPermission(requestCode: Int): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                fragment.requireContext(),
                STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fragment.requestPermissions(arrayOf(STORAGE), requestCode)
            false
        } else true
    }

    private fun exportEpub() {
        if (checkPermission(EXPORT_EPUB_REQUEST)) {
            FFLogger.d(FFLogger.EVENT_KEY, "Export EPUB for $exportFanfictionId")
            optionsViewModel.buildEpub(absolutePath, exportFanfictionId)
        }
    }

    private fun exportPdf() {
        if (checkPermission(EXPORT_PDF_REQUEST)) {
            FFLogger.d(FFLogger.EVENT_KEY, "Export PDF for $exportFanfictionId")
            optionsViewModel.buildPdf(absolutePath, exportFanfictionId)
        }
    }
}
