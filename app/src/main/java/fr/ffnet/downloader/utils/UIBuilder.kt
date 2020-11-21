package fr.ffnet.downloader.utils

import android.content.res.Resources
import fr.ffnet.downloader.R
import fr.ffnet.downloader.profile.AuthorUIItem
import fr.ffnet.downloader.repository.entities.Author
import fr.ffnet.downloader.repository.entities.AuthorSearchResult
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.synced.FanfictionUIItem.FanfictionUI
import javax.inject.Inject

class UIBuilder @Inject constructor(
    private val resources: Resources,
    private val dateFormatter: DateFormatter
) {

    fun buildSearchAuthorUI(
        author: AuthorSearchResult
    ): AuthorUIItem.SearchAuthorUIItem {
        return AuthorUIItem.SearchAuthorUIItem(
            id = author.id,
            name = author.name,
            nbStories = author.nbStories
        )
    }

    fun buildSyncedAuthor(
        author: Author
    ): AuthorUIItem.SyncedAuthorUIItem {
        return AuthorUIItem.SyncedAuthorUIItem(
            id = author.id,
            name = author.name,
            fetchedDate = dateFormatter.format(author.fetchedDate),
            nbStories = author.nbStories,
            nbFavorites = author.nbFavorites
        )
    }

    fun buildFanfictionUI(
        fanfiction: Fanfiction,
        shouldShowAuthor: Boolean = false,
        shouldShowWords: Boolean = false,
        shouldShowChapterSync: Boolean = true,
        shouldShowExport: Boolean = true
    ): FanfictionUI {
        val isDownloadComplete = fanfiction.nbSyncedChapters == fanfiction.nbChapters
        return FanfictionUI(
            id = fanfiction.id,
            title = fanfiction.title,
            words = fanfiction.words.toString(),
            author = fanfiction.author,
            summary = fanfiction.summary,
            updatedDate = dateFormatter.format(fanfiction.updatedDate),
            publishedDate = dateFormatter.format(fanfiction.publishedDate),
            fetchedDate = dateFormatter.format(fanfiction.fetchedDate),
            progressionText = resources.getString(
                R.string.download_info_chapters_value,
                fanfiction.nbSyncedChapters,
                fanfiction.nbChapters
            ),
            chaptersNb = fanfiction.nbChapters,
            isDownloadComplete = isDownloadComplete,
            exportPdfImage = if (isDownloadComplete) R.drawable.ic_export_pdf_enabled else R.drawable.ic_export_pdf_disabled,
            exportEpubImage = if (isDownloadComplete) R.drawable.ic_export_epub_enabled else R.drawable.ic_export_epub_disabled,
            shouldShowAuthor = shouldShowAuthor,
            shouldShowWords = shouldShowWords,
            shouldShowChapterSync = shouldShowChapterSync,
            shouldShowExport = shouldShowExport
        )
    }
}
