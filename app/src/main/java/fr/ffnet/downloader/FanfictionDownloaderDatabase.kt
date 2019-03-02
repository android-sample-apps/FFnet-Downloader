package fr.ffnet.downloader

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.ffnet.downloader.repository.ChapterEntity
import fr.ffnet.downloader.repository.FanfictionDao
import fr.ffnet.downloader.repository.FanfictionEntity
import fr.ffnet.downloader.repository.HistoryEntity
import fr.ffnet.downloader.utils.Converters

@Database(
    entities = [FanfictionEntity::class, ChapterEntity::class, HistoryEntity::class],
    version = 12
)
@TypeConverters(Converters::class)
abstract class FanfictionDownloaderDatabase : RoomDatabase() {
    abstract fun fanfictionDao(): FanfictionDao
}
