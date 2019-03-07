package fr.ffnet.downloader

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.ffnet.downloader.repository.*
import fr.ffnet.downloader.utils.Converters

@Database(
    entities = [
        FanfictionEntity::class,
        ChapterEntity::class,
        HistoryEntity::class,
        ProfileEntity::class,
        ProfileFanfictionEntity::class
    ],
    version = 20
)
@TypeConverters(Converters::class)
abstract class FanfictionDownloaderDatabase : RoomDatabase() {
    abstract fun fanfictionDao(): FanfictionDao
    abstract fun historyDao(): HistoryDao
    abstract fun profileDao(): ProfileDao
}
