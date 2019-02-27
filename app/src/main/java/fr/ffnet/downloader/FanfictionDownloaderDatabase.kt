package fr.ffnet.downloader

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.ffnet.downloader.downloader.ChapterEntity
import fr.ffnet.downloader.downloader.FanfictionDao
import fr.ffnet.downloader.downloader.FanfictionEntity
import fr.ffnet.downloader.utils.Converters

@Database(
    entities = [FanfictionEntity::class, ChapterEntity::class],
    version = 8
)
@TypeConverters(Converters::class)
abstract class FanfictionDownloaderDatabase : RoomDatabase() {
    abstract fun fanfictionDao(): FanfictionDao
}
