package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var selectedOption: EnumOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (radioGroup.checkedRadioButtonId != -1) {
                when (radioGroup.checkedRadioButtonId) {
                    radioGlide.id -> {
                        selectedOption = EnumOptions.GLIDE
                    }
                    radioUdacity.id -> {
                        selectedOption = EnumOptions.UDACITY
                    }
                    radioRetrofit.id -> {
                        selectedOption = EnumOptions.RETROFIT
                    }
                }
                selectedOption?.let { option -> download(option) }
                custom_button.startLoading()
            } else {
                Toast.makeText(this, getString(R.string.txt_invalid_option), Toast.LENGTH_SHORT).show()
            }

            createChannel(
                    getString(R.string.txt_download_notification_channel_id),
                    getString(R.string.txt_notification_channel)
            )
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)

            val notificationManager = getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val query = DownloadManager.Query()
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val isSuccess = status == DownloadManager.STATUS_SUCCESSFUL
                    selectedOption?.let { sendNotifications(isSuccess, it) }
                }
                Toast.makeText(this@MainActivity, "Download Completed", Toast.LENGTH_SHORT).show()
                custom_button.setDelayedCompleted()
            }

        }
    }

    private fun sendNotifications(isSuccess: Boolean, enumOptions: EnumOptions) {
        val notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()
        notificationManager.sendNotification(this,
                isSuccess, enumOptions)
    }

    private fun download(enumOptions: EnumOptions) {
        val request =
                DownloadManager.Request(Uri.parse(enumOptions.url))
                        .setTitle(getString(enumOptions.title))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }
}
