package fr.ffnet.downloader.utils

import android.content.res.Resources
import fr.ffnet.downloader.R
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.synced.FanfictionUIItem.FanfictionUI
import javax.inject.Inject

class FanfictionUIBuilder @Inject constructor(
    private val resources: Resources,
    private val dateFormatter: DateFormatter
) {

    fun buildFanfictionUI(fanfiction: Fanfiction): FanfictionUI {
        val isDownloadComplete = fanfiction.nbSyncedChapters == fanfiction.nbChapters
        return FanfictionUI(
            id = fanfiction.id,
            title = fanfiction.title,
            words = fanfiction.words.toString(),
            summary = fanfiction.summary,
            updatedDate = dateFormatter.format(fanfiction.updatedDate),
            publishedDate = dateFormatter.format(fanfiction.publishedDate),
            fetchedDate = dateFormatter.format(fanfiction.fetchedDate),
            progressionText = resources.getString(
                R.string.download_info_chapters_value,
                fanfiction.nbSyncedChapters,
                fanfiction.nbChapters
            ),
            isDownloadComplete = isDownloadComplete,
            exportPdfImage = if (isDownloadComplete) R.drawable.ic_export_pdf_enabled else R.drawable.ic_export_pdf_disabled,
            exportEpubImage = if (isDownloadComplete) R.drawable.ic_export_epub_enabled else R.drawable.ic_export_epub_disabled
        )
    }
}
