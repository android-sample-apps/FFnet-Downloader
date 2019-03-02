package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fr.ffnet.downloader.search.Chapter
import fr.ffnet.downloader.search.Fanfiction

class DatabaseRepository(private val dao: FanfictionDao) {

    fun getFanfictionFromDb(fanfictionId: String): Fanfiction {
        return dao.getFanfiction(fanfictionId).toFanfiction(
            dao.getChapters(fanfictionId)
        )
    }

    fun getFanfictionsFromDbLiveData(): LiveData<List<Fanfiction>> {
        return Transformations.map(dao.getFanfictionsLiveData()) { fanfictionList ->
            fanfictionList.map { buildFanfiction(it) }
        }
    }

    fun deleteFanfiction(fanfictionId: String) {
        dao.deleteFanfiction(fanfictionId)
    }

    private fun buildFanfiction(fanfictionEntity: FanfictionEntity): Fanfiction {
        return Fanfiction(
            id = fanfictionEntity.id,
            title = fanfictionEntity.title,
            words = fanfictionEntity.words,
            publishedDate = fanfictionEntity.publishedDate,
            summary = fanfictionEntity.summary,
            updatedDate = fanfictionEntity.updatedDate,
            nbChapters = fanfictionEntity.nbChapters,
            nbSyncedChapters = fanfictionEntity.nbSyncedChapters
        )
    }

    private fun FanfictionEntity.toFanfiction(chapterList: List<ChapterEntity>) = Fanfiction(
        id = id,
        title = title,
        words = words,
        publishedDate = publishedDate,
        summary = summary,
        updatedDate = updatedDate,
        nbChapters = chapterList.size,
        nbSyncedChapters = chapterList.filter(ChapterEntity::isSynced).size,
        chapterList = chapterList.map { it.toChapter() }
    )

    private fun ChapterEntity.toChapter(): Chapter = Chapter(
        id = chapterId,
        title = title,
        status = content.isNotEmpty()
    )
}
