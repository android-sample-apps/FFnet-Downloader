package fr.ffnet.downloader.fanfiction.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionDisplayModel
import javax.inject.Inject

class DownloadNotification @Inject constructor(
    private val context: Context,
    private val resources: Resources
) {

    companion object {
        private const val CHANNEL_ID = "downloadProgressionChannelId"
    }

    fun showNotification(model: FanfictionDisplayModel) {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText(
                resources.getString(
                    R.string.notification_download_progression_title,
                    model.progressionText
                )
            )
            .setProgress(model.nbChapters, model.progression, false)
        NotificationManagerCompat.from(context).notify(
            model.id.toInt(),
            builder.build()
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = resources.getString(R.string.notification_download_progression_channel_name)
            val descriptionText = resources.getString(R.string.notification_download_progression_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
