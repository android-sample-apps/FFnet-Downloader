package fr.ffnet.downloader.downloader

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface FanfictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFanfiction(fanfiction: FanfictionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChapterList(chapterList: ChapterEntity)

}
