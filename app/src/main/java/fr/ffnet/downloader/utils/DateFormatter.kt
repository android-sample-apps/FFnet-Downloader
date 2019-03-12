package fr.ffnet.downloader.utils

import org.joda.time.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DateFormatter @Inject constructor() {

    companion object {
        private val DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private const val DATETIME_FORMATTER = "yyyy-MM-dd HH:mm"
        private const val NO_DATE = "N/A"
    }

    fun format(date: Date): String = DATE_FORMATTER.format(date)

    fun format(date: LocalDateTime?): String = date?.toString(DATETIME_FORMATTER) ?: NO_DATE
}
