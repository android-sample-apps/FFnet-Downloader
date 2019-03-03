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

    @Query("UPDATE ChapterEntity SET content = :content, isSynced = :isSynced WHERE id = :fanfictionId AND chapterId = :chapterId")
    fun updateChapter(content: String, isSynced: Boolean, fanfictionId: String, chapterId: String)

    @Query("DELETE FROM fanfictionentity WHERE id = :fanfictionId")
    fun deleteFanfiction(fanfictionId: String): Int

    @Query("SELECT FanfictionEntity.*, COUNT(*) AS nbChapters, SUM(isSynced) AS nbSyncedChapters FROM FanfictionEntity LEFT JOIN ChapterEntity ON (ChapterEntity.id = FanfictionEntity.id) WHERE FanfictionEntity.id = :fanfictionId")
    fun getFanfiction(fanfictionId: String): FanfictionEntity

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = id) AS nbChapters, " +
            "(SELECT SUM(isSynced) FROM ChapterEntity WHERE FanfictionEntity.id = id) AS nbSyncedChapters " +
            "FROM FanfictionEntity"
    )
    fun getFanfictionsLiveData(): LiveData<List<FanfictionEntity>>

    @Query("SELECT * FROM ChapterEntity WHERE id = :fanfictionId")
    fun getChaptersLivedata(fanfictionId: String): LiveData<List<ChapterEntity>>

    @Query("SELECT * FROM ChapterEntity WHERE id = :fanfictionId")
    fun getChapters(fanfictionId: String): List<ChapterEntity>
}
