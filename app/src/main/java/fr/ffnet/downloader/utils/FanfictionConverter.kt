package fr.ffnet.downloader.utils

import fr.ffnet.downloader.repository.entities.ChapterEntity
import fr.ffnet.downloader.repository.entities.FanfictionEntity
import fr.ffnet.downloader.search.Chapter
import fr.ffnet.downloader.search.Fanfiction
import javax.inject.Inject

class FanfictionConverter @Inject constructor() {

    fun toFanfiction(
        fanfiction: FanfictionEntity,
        chapterList: List<ChapterEntity> = emptyList()
    ) = Fanfiction(
        id = fanfiction.id,
        title = fanfiction.title,
        words = fanfiction.words,
        summary = fanfiction.summary,
        publishedDate = fanfiction.publishedDate,
        updatedDate = fanfiction.updatedDate,
        fetchedDate = fanfiction.fetchedDate,
        profileType = fanfiction.profileType,
        nbChapters = fanfiction.nbChapters,
        nbSyncedChapters = fanfiction.nbSyncedChapters,
        chapterList = chapterList.map { toChapter(it) }
    )

    private fun toChapter(chapter: ChapterEntity): Chapter = Chapter(
        id = chapter.chapterId,
        title = chapter.title,
        content = chapter.content
    )

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
            content = chapter.content
        )
    }
}
