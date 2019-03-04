package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FanfictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFanfiction(fanfiction: FanfictionEntity)

    @Insert
    fun insertChapterList(chapterList: List<ChapterEntity>)

    @Query("UPDATE ChapterEntity SET content = :content, isSynced = :isSynced WHERE fanfictionId = :fanfictionId AND chapterId = :chapterId")
    fun updateChapter(content: String, isSynced: Boolean, fanfictionId: String, chapterId: String)

    @Query("DELETE FROM FanfictionEntity WHERE id = :fanfictionId")
    fun deleteFanfiction(fanfictionId: String): Int

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbChapters, " +
            "(SELECT SUM(isSynced) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbSyncedChapters " +
            "FROM FanfictionEntity WHERE id = :fanfictionId"
    )
    fun getFanfiction(fanfictionId: String): FanfictionEntity?

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbChapters, " +
            "(SELECT SUM(isSynced) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbSyncedChapters " +
            "FROM FanfictionEntity WHERE syncedDate IS NOT NULL"
    )
    fun getFanfictionsLiveData(): LiveData<List<FanfictionEntity>>

    @Query("SELECT * FROM ChapterEntity WHERE fanfictionId = :fanfictionId")
    fun getChaptersLivedata(fanfictionId: String): LiveData<List<ChapterEntity>>

    @Query("SELECT * FROM ChapterEntity WHERE fanfictionId = :fanfictionId")
    fun getChapters(fanfictionId: String): List<ChapterEntity>

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbChapters, " +
            "(SELECT SUM(isSynced) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbSyncedChapters " +
            "FROM FanfictionEntity " +
            "WHERE id IN (SELECT fanfictionId FROM ProfileFanfictionEntity LEFT JOIN ProfileEntity ON (ProfileFanfictionEntity.profileId = ProfileEntity.profileId) WHERE isAssociated = 1)"
    )
    fun getFanfictionsFromAssociatedProfileLiveData(): LiveData<List<FanfictionEntity>>
}
