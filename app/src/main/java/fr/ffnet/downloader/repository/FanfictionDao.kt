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

    @Query("SELECT FanfictionEntity.*, COUNT(*) AS nbChapters, SUM(isSynced) AS nbSyncedChapters FROM FanfictionEntity LEFT JOIN ChapterEntity ON (ChapterEntity.fanfictionId = FanfictionEntity.id) WHERE FanfictionEntity.id = :fanfictionId")
    fun getFanfiction(fanfictionId: String): FanfictionEntity

    @Query("SELECT FanfictionEntity.*, COUNT(*) AS nbChapters, SUM(isSynced) AS nbSyncedChapters FROM FanfictionEntity LEFT JOIN ChapterEntity ON (ChapterEntity.fanfictionId = FanfictionEntity.id)")
    fun getFanfictionsLiveData(): LiveData<List<FanfictionEntity>>

    @Query("UPDATE ChapterEntity SET content = :content, isSynced = :isSynced WHERE fanfictionId = :fanfictionId AND chapterId = :chapterId")
    fun updateChapter(content: String, isSynced: Boolean, fanfictionId: String, chapterId: String)

    @Query("SELECT * FROM ChapterEntity WHERE fanfictionId = :fanfictionId")
    fun getChaptersLivedata(fanfictionId: String): LiveData<List<ChapterEntity>>

    @Query("SELECT * FROM ChapterEntity WHERE fanfictionId = :fanfictionId")
    fun getChapters(fanfictionId: String): List<ChapterEntity>

    @Query("DELETE FROM fanfictionentity WHERE id = :fanfictionId")
    fun deleteFanfiction(fanfictionId: String): Int
}
