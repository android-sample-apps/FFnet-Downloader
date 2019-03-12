package fr.ffnet.downloader.fanfictionutils

import fr.ffnet.downloader.repository.entities.ChapterEntity
import fr.ffnet.downloader.repository.entities.FanfictionEntity
import fr.ffnet.downloader.search.Chapter
import fr.ffnet.downloader.search.Fanfiction
import javax.inject.Inject

class FanfictionTransformer @Inject constructor() {

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
