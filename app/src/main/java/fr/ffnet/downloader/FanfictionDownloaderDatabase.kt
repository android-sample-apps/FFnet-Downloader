package fr.ffnet.downloader

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.ffnet.downloader.downloader.FanfictionDao
import fr.ffnet.downloader.downloader.FanfictionEntity

@Database(entities = [FanfictionEntity::class], version = 1)
abstract class FanfictionDownloaderDatabase : RoomDatabase() {
    abstract fun fanfictionDao(): FanfictionDao
}
