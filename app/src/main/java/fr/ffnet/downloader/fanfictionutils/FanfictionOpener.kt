package fr.ffnet.downloader.fanfictionutils

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File

class FanfictionOpener(private val context: Context) {

    fun openFile(fileName: String) {

        val absolutePath = context.getExternalFilesDir(
            Environment.DIRECTORY_DOCUMENTS
        )?.absolutePath ?: throw IllegalArgumentException()

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File(absolutePath, fileName)
        )

        context.grantUriPermission(
            context.packageName,
            contentUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        val mimeValue = getMimeType(contentUri.toString())
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = contentUri
            type = mimeValue
        }
        context.startActivity(Intent.createChooser(intent, "Open with..."))

        //        val contentUri = FileProvider.getUriForFile(
        //            requireContext(),
        //            "${requireContext().packageName}.fileprovider",
        //            file
        //        )
        //        if (uri != null) {
        //            val intent = Intent(Intent.ACTION_VIEW).apply {
        //                data = contentUri
        //                type = mimeValue
        //            }
        //            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //            requireContext().startActivity(Intent.createChooser(intent, "Open with..."))
        //        }
    }

    private fun getMimeType(url: String): String? {

        val ext = MimeTypeMap.getFileExtensionFromUrl(url)
        var mime: String? = null
        if (ext != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        }
        return mime
    }
}
