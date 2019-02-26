package fr.ffnet.downloader.downloader

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FanfictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFanfiction(fanfiction: FanfictionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChapterList(chapterList: List<ChapterEntity>)

    @Query("SELECT * FROM ChapterEntity WHERE fanfictionId = :fanfictionId")
    fun getChapters(fanfictionId: String): LiveData<List<ChapterEntity>>

}
