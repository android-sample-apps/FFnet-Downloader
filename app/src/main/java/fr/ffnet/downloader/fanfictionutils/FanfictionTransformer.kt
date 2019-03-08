package fr.ffnet.downloader.fanfictionutils

import android.content.res.Resources
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionInfoUIModel
import fr.ffnet.downloader.repository.ChapterEntity
import fr.ffnet.downloader.repository.DownloaderRepository.ChapterDownloadState
import fr.ffnet.downloader.repository.FanfictionEntity
import fr.ffnet.downloader.search.Chapter
import fr.ffnet.downloader.search.Fanfiction
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FanfictionTransformer @Inject constructor(
    private val resources: Resources
) {

    fun toFanfictionEntity(fanfiction: Fanfiction): FanfictionEntity = FanfictionEntity(
        id = fanfiction.id,
        title = fanfiction.title,
        words = fanfiction.words,
        summary = fanfiction.summary,
        publishedDate = fanfiction.publishedDate,
        updatedDate = fanfiction.updatedDate,
        fetchedDate = fanfiction.fetchedDate,
        nbChapters = fanfiction.nbChapters,
        nbSyncedChapters = fanfiction.nbSyncedChapters
    )

    fun toChapterEntityList(
        fanfictionId: String,
        chapterList: List<Chapter>
    ): List<ChapterEntity> = chapterList.map { chapter ->
        ChapterEntity(
            fanfictionId = fanfictionId,
            chapterId = chapter.id,
            title = chapter.title,
            content = chapter.content,
            isSynced = chapter.status
        )
    }

    fun toFanfictionUIModel(
        fanfiction: Fanfiction
    ): FanfictionInfoUIModel.FanfictionUIModel {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return FanfictionInfoUIModel.FanfictionUIModel(
            id = fanfiction.id,
            title = fanfiction.title,
            words = fanfiction.words.toString(),
            summary = fanfiction.summary,
            updatedDate = formatter.format(fanfiction.updatedDate),
            publishedDate = formatter.format(fanfiction.publishedDate),
            syncedDate = fanfiction.fetchedDate?.toString("yyyy-MM-dd HH:mm") ?: "N/A",
            progression = resources.getString(
                R.string.download_info_chapters_value,
                fanfiction.nbSyncedChapters,
                fanfiction.nbChapters
            )
        )
    }

    fun toChapterUIModel(chapterEntityList: List<ChapterEntity>): List<FanfictionInfoUIModel.ChapterUIModel> {
        return chapterEntityList.map { toChapterUIModel(it) }
    }

    private fun toChapterUIModel(chapterEntity: ChapterEntity): FanfictionInfoUIModel.ChapterUIModel {
        return FanfictionInfoUIModel.ChapterUIModel(
            id = chapterEntity.chapterId,
            title = chapterEntity.title,
            status = resources.getString(
                when (chapterEntity.isSynced) {
                    true -> R.string.download_info_chapter_status_synced
                    false -> R.string.download_info_chapter_status_not_synced
                }
            )
        )
    }

    fun toFanfiction(fanfictionEntity: FanfictionEntity): Fanfiction = Fanfiction(
        id = fanfictionEntity.id,
        title = fanfictionEntity.title,
        words = fanfictionEntity.words,
        summary = fanfictionEntity.summary,
        publishedDate = fanfictionEntity.publishedDate,
        updatedDate = fanfictionEntity.updatedDate,
        fetchedDate = fanfictionEntity.fetchedDate,
        profileType = fanfictionEntity.profileType,
        nbChapters = fanfictionEntity.nbChapters,
        nbSyncedChapters = fanfictionEntity.nbSyncedChapters,
        chapterList = emptyList()
    )

}
