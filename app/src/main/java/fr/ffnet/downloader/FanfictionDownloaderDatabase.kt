package fr.ffnet.downloader

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.ffnet.downloader.downloader.ChapterEntity
import fr.ffnet.downloader.downloader.FanfictionDao
import fr.ffnet.downloader.downloader.FanfictionEntity

@Database(entities = [FanfictionEntity::class, ChapterEntity::class], version = 6)
abstract class FanfictionDownloaderDatabase : RoomDatabase() {
    abstract fun fanfictionDao(): FanfictionDao
}
