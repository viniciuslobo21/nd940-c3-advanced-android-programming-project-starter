package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0
private val STATUS = "download_status"
private val NAME = "download_name"

fun NotificationManager.sendNotification(
        applicationContext: Context,
        isSuccess: Boolean,
        enumOptions: EnumOptions
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(STATUS, isSuccess)
    contentIntent.putExtra(NAME, applicationContext.resources.getString(enumOptions.title))

    contentIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

    val buttonPendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            contentIntent,
            FLAGS)

    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificationDescription = applicationContext.resources.getString(
            R.string.notification_description, enumOptions.option
    )

    val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.txt_download_notification_channel_id)
    )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext
                    .getString(R.string.notification_title))
            .setContentText(notificationDescription)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(
                    R.drawable.ic_assistant_black_24dp,
                    applicationContext.getString(R.string.txt_notification_button),
                    buttonPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
