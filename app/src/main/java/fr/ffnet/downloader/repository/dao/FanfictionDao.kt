package fr.ffnet.downloader.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.ffnet.downloader.repository.entities.ChapterEntity
import fr.ffnet.downloader.repository.entities.FanfictionEntity

@Dao
interface FanfictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFanfiction(fanfiction: FanfictionEntity)

    @Insert
    fun insertChapterList(chapterList: List<ChapterEntity>)

    @Query("UPDATE ChapterEntity SET isSynced = 1, content = :content WHERE fanfictionId = :fanfictionId AND chapterId = 1")
    fun updateFirstChapter(fanfictionId: String, content: String)

    @Query("UPDATE ChapterEntity SET content = :content, isSynced = :isSynced WHERE fanfictionId = :fanfictionId AND chapterId = :chapterId")
    fun updateChapter(content: String, isSynced: Boolean, fanfictionId: String, chapterId: String)

    @Query("UPDATE ChapterEntity SET content = '', isSynced = 0 WHERE fanfictionId = :fanfictionId")
    fun unsyncFanfiction(fanfictionId: String): Int

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT SUM(isSynced) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbSyncedChapters " +
            "FROM FanfictionEntity WHERE id = :fanfictionId"
    )
    fun getFanfiction(fanfictionId: String): FanfictionEntity?

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT SUM(isSynced) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbSyncedChapters " +
            "FROM FanfictionEntity WHERE id = :fanfictionId"
    )
    fun getFanfictionLiveData(fanfictionId: String): LiveData<FanfictionEntity>

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT SUM(isSynced) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbSyncedChapters " +
            "FROM FanfictionEntity " +
            "WHERE id IN (SELECT fanfictionId FROM ChapterEntity GROUP BY fanfictionId HAVING SUM(isSynced) > 1 OR COUNT(*) = 1)"
    )
    fun getSyncedFanfictions(): LiveData<List<FanfictionEntity>>

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT SUM(isSynced) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbSyncedChapters " +
            "FROM FanfictionEntity WHERE fetchedDate IS NOT NULL"
    )
    fun getFanfictionHistory(): LiveData<List<FanfictionEntity>>

    @Query("SELECT * FROM ChapterEntity WHERE fanfictionId = :fanfictionId")
    fun getChaptersLivedata(fanfictionId: String): LiveData<List<ChapterEntity>>

    @Query("SELECT * FROM ChapterEntity WHERE fanfictionId = :fanfictionId AND content IS NULL")
    fun getChaptersToSync(fanfictionId: String): List<ChapterEntity>

    @Query("SELECT chapterId FROM ChapterEntity WHERE fanfictionId = :fanfictionId")
    fun getChaptersIds(fanfictionId: String): List<String>

    @Query(
        "SELECT " +
            "FanfictionEntity.*, ProfileFanfictionEntity.profileType, " +
            "(SELECT SUM(isSynced) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbSyncedChapters " +
            "FROM FanfictionEntity " +
            "LEFT JOIN ProfileFanfictionEntity ON (ProfileFanfictionEntity.fanfictionId = FanfictionEntity.id) " +
            "LEFT JOIN ProfileEntity ON (ProfileFanfictionEntity.profileId = ProfileEntity.profileId) " +
            "WHERE ProfileEntity.isAssociated = 1 " +
            "AND ProfileFanfictionEntity.profileType = :profileType " +
            "ORDER BY FanfictionEntity.fetchedDate DESC, FanfictionEntity.updatedDate DESC"
    )
    fun getFanfictionsFromAssociatedProfileLiveData(profileType: Int): LiveData<List<FanfictionEntity>>
}
