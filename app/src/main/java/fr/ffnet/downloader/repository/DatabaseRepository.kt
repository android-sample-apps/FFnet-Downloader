package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fr.ffnet.downloader.repository.ProfileRepository.Companion.PROFILE_TYPE_FAVORITE
import fr.ffnet.downloader.repository.ProfileRepository.Companion.PROFILE_TYPE_MY_STORY
import fr.ffnet.downloader.search.Chapter
import fr.ffnet.downloader.search.Fanfiction

class DatabaseRepository(private val dao: FanfictionDao) {

    fun getFanfictionInfo(fanfictionId: String): LiveData<Fanfiction> {
        return Transformations.map(dao.getFanfictionLiveData(fanfictionId)) {
            it.toFanfiction(emptyList())
        }
    }

    fun getFanfictionsFromDbLiveData(): LiveData<List<Fanfiction>> = transformEntityLiveDataToModelLiveData(
        dao.getFanfictionsLiveData()
    )

    fun getMyFavoriteFanfictions(): LiveData<List<Fanfiction>> {
        return transformEntityLiveDataToModelLiveData(
            dao.getFanfictionsFromAssociatedProfileLiveData(PROFILE_TYPE_FAVORITE)
        )
    }

    fun getMyFanfictions(): LiveData<List<Fanfiction>> {
        return transformEntityLiveDataToModelLiveData(
            dao.getFanfictionsFromAssociatedProfileLiveData(PROFILE_TYPE_MY_STORY)
        )
    }

    fun unsyncFanfiction(fanfictionId: String) {
        dao.unsyncFanfiction(fanfictionId)
    }

    private fun transformEntityLiveDataToModelLiveData(
        liveData: LiveData<List<FanfictionEntity>>
    ): LiveData<List<Fanfiction>> = Transformations.map(liveData) { fanfictionList ->
        if (fanfictionList.isEmpty()) {
            emptyList()
        } else {
            fanfictionList.map { it.toFanfiction(emptyList()) }
        }
    }

    private fun FanfictionEntity.toFanfiction(chapterList: List<ChapterEntity>) = Fanfiction(
        id = id,
        title = title,
        words = words,
        summary = summary,
        publishedDate = publishedDate,
        updatedDate = updatedDate,
        fetchedDate = fetchedDate,
        profileType = profileType,
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
