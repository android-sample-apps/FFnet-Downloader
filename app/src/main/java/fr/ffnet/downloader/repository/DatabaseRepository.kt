package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fr.ffnet.downloader.repository.AuthorRepository.Companion.PROFILE_TYPE_FAVORITE
import fr.ffnet.downloader.repository.AuthorRepository.Companion.PROFILE_TYPE_MY_STORY
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.entities.ChapterEntity
import fr.ffnet.downloader.repository.entities.FanfictionEntity
import fr.ffnet.downloader.search.Fanfiction
import fr.ffnet.downloader.utils.FanfictionConverter

class DatabaseRepository(
    private val dao: FanfictionDao,
    private val fanfictionConverter: FanfictionConverter
) {

    fun getFanfictionInfo(fanfictionId: String): LiveData<Fanfiction> {
        return Transformations.map(dao.getFanfictionLiveData(fanfictionId)) {
            fanfictionConverter.toFanfiction(it)
        }
    }

    fun getSyncedFanfictions(): LiveData<List<Fanfiction>> = transformEntityToModel(
        dao.getSyncedFanfictions()
    )

    fun loadHistory(): LiveData<List<Fanfiction>> = transformEntityToModel(
        dao.getFanfictionHistory()
    )

    fun getChapters(fanfictionId: String): LiveData<List<ChapterEntity>> = dao.getChaptersLivedata(
        fanfictionId
    )

    fun getFavoriteFanfictions(authorId: String): LiveData<List<Fanfiction>> {
        return transformEntityToModel(
            dao.getFanfictionsFromAssociatedProfileLiveData(authorId, PROFILE_TYPE_FAVORITE)
        )
    }

    fun getStories(authorId: String): LiveData<List<Fanfiction>> {
        return transformEntityToModel(
            dao.getFanfictionsFromAssociatedProfileLiveData(authorId, PROFILE_TYPE_MY_STORY)
        )
    }

    fun unsyncFanfiction(fanfictionId: String) {
        dao.unsyncFanfiction(fanfictionId)
    }

    fun getCompleteFanfiction(fanfictionId: String): Fanfiction? {
        val fanfiction = dao.getFanfiction(fanfictionId)
        val chapterList = dao.getSyncedChapters(fanfictionId)
        return if (chapterList.isNotEmpty() && fanfiction != null) {
            fanfictionConverter.toFanfiction(fanfiction, chapterList)
        } else null
    }

    fun isFanfictionInDatabase(fanfictionId: String): Boolean {
        return dao.getFanfiction(fanfictionId) != null
    }

    private fun transformEntityToModel(
        liveData: LiveData<List<FanfictionEntity>>
    ): LiveData<List<Fanfiction>> = Transformations.map(liveData) { fanfictionList ->
        fanfictionList.map { fanfictionConverter.toFanfiction(it) }
    }
}
