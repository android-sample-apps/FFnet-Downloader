package fr.ffnet.downloader.utils

import androidx.room.TypeConverter
import org.joda.time.LocalDateTime
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTimestampToLocalDate(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toDate()?.time
    }
}
