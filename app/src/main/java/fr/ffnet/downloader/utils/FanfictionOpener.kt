package fr.ffnet.downloader.utils

import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File

class FanfictionOpener(private val context: Context) {

    fun openFile(fileName: String, absolutePath: String) {
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

        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, getMimeType(contentUri.toString()))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.also {
            context.startActivity(it)
        }
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
