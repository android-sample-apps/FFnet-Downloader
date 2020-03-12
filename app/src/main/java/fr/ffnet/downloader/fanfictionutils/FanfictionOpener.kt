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
            "fr.ffnet.downloader.fileprovider",
            File(absolutePath, fileName)
        )

        context.grantUriPermission(
            context.packageName,
            contentUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        val mimeValue = getMimeType(contentUri.toString())
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(contentUri, mimeValue)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
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
