package fr.ffnet.downloader.synced

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.lifecycle.LifecycleOwner
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.fanfictionoptions.OptionsViewModel
import fr.ffnet.downloader.fanfictionoptions.OptionsViewModel.SearchError
import fr.ffnet.downloader.synced.FanfictionUIItem.FanfictionUI
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.OnFanfictionActionsListener

interface ParentListener {
    fun onPermissionRequested(arrayOf: Array<String>, requestCode: Int): Boolean
    fun showErrorMessage(message: String)
}

class OptionsController(
    private val context: Context,
    lifecycleOwner: LifecycleOwner,
    private val parentListener: ParentListener,
    private val optionsViewModel: OptionsViewModel,
    private val fanfictionOpener: FanfictionOpener
) : OnFanfictionActionsListener {

    companion object {
        const val STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val EXPORT_EPUB_REQUEST = 2000
        private const val EXPORT_PDF_REQUEST = 2001
    }

    private val absolutePath: String by lazy {
        context.getExternalFilesDir(
            Environment.DIRECTORY_DOCUMENTS
        )?.absolutePath ?: throw IllegalArgumentException()
    }
    private var exportFanfictionId: String = ""

    init {

        optionsViewModel.getFile.observe(lifecycleOwner, { (fileName, absolutePath) ->
            fanfictionOpener.openFile(fileName, absolutePath)
        })
        optionsViewModel.navigateToFanfiction.observe(lifecycleOwner, { fanfictionId ->
            context.startActivity(
                FanfictionActivity.intent(
                    context,
                    fanfictionId
                )
            )
        })
        optionsViewModel.error.observe(lifecycleOwner, { searchError ->
            when (searchError) {
                is SearchError.UrlNotValid,
                is SearchError.InfoFetchingFailed -> {
                    parentListener.showErrorMessage(searchError.message)
                }
            }
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

    override fun onFetchInformation(fanfictionId: String) {
        FFLogger.d(FFLogger.EVENT_KEY, "Opening details for $fanfictionId")
        optionsViewModel.loadFanfictionInfo(fanfictionId)
    }

    override fun onExportPdf(fanfictionId: String) {
        exportFanfictionId = fanfictionId
        exportPdf()
    }

    override fun onExportEpub(fanfictionId: String) {
        exportFanfictionId = fanfictionId
        exportEpub()
    }

    override fun onUnsync(fanfiction: FanfictionUI) {
        FFLogger.d(FFLogger.EVENT_KEY, "Unsync ${fanfiction.id}")
        optionsViewModel.unsyncFanfiction(fanfiction.id)
    }

    private fun checkPermission(requestCode: Int): Boolean {
        return parentListener.onPermissionRequested(arrayOf(STORAGE), requestCode)
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
