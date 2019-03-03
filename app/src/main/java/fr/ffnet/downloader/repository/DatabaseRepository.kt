package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fr.ffnet.downloader.search.Chapter
import fr.ffnet.downloader.search.Fanfiction

class DatabaseRepository(private val dao: FanfictionDao) {

    fun getFanfictionFromDb(fanfictionId: String): Fanfiction {
        return dao.getFanfiction(fanfictionId)!!.toFanfiction(
            dao.getChapters(fanfictionId)
        )
    }

    fun getFanfictionsFromDbLiveData(): LiveData<List<Fanfiction>> = transformEntityLiveDataToModelLiveData(
        dao.getFanfictionsLiveData()
    )

    fun deleteFanfiction(fanfictionId: String) {
        dao.deleteFanfiction(fanfictionId)
    }

    fun getFanfictionsFromProfile(profileId: String): LiveData<List<Fanfiction>> {
        return transformEntityLiveDataToModelLiveData(
            dao.getFanfictionsFromProfileLiveData(profileId)
        )
    }

    fun transformEntityLiveDataToModelLiveData(
        liveData: LiveData<List<FanfictionEntity>>
    ): LiveData<List<Fanfiction>> = Transformations.map(liveData) { fanfictionList ->
        if (fanfictionList.isEmpty()) {
            emptyList()
        } else {
            fanfictionList.map { it.toFanfiction(emptyList()) }
        }
    }

    private fun buildFanfiction(fanfictionEntity: FanfictionEntity): Fanfiction {
        return Fanfiction(
            id = fanfictionEntity.id,
            title = fanfictionEntity.title,
            words = fanfictionEntity.words,
            summary = fanfictionEntity.summary,
            publishedDate = fanfictionEntity.publishedDate,
            updatedDate = fanfictionEntity.updatedDate,
            syncedDate = fanfictionEntity.syncedDate,
            nbChapters = fanfictionEntity.nbChapters,
            nbSyncedChapters = fanfictionEntity.nbSyncedChapters
        )
    }

    private fun FanfictionEntity.toFanfiction(chapterList: List<ChapterEntity>) = Fanfiction(
        id = id,
        title = title,
        words = words,
        summary = summary,
        publishedDate = publishedDate,
        updatedDate = updatedDate,
        syncedDate = syncedDate,
        nbChapters = nbChapters,
        nbSyncedChapters = nbSyncedChapters,
        chapterList = chapterList.map { it.toChapter() }
    )

    private fun ChapterEntity.toChapter(): Chapter = Chapter(
        id = chapterId,
        title = title,
        status = content.isNotEmpty()
    )
}
