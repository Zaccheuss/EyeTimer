package com.zaccheus.eyetimer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.zaccheus.eyetimer.util.CustomDebugTree
import timber.log.Timber

class MainApplication : Application() {

    companion object {
        const val HIGH_PRIORITY_CHANNEL_ID = "com.zaccheus.eyetimer.HighPriorityNotificationChannel"
        const val LOW_PRIORITY_CHANNEL_ID = "com.zaccheus.eyetimer.LowPriorityNotificationChannel"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(CustomDebugTree())
        Timber.v("Application started and Timber tree planted")
        setupNotificationChannels()
    }

    private fun setupNotificationChannels() {
        // If the device version is below Oreo we don't need to create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val highPriorityChannel = NotificationChannel(
                HIGH_PRIORITY_CHANNEL_ID,
                "Timer End Notifications",
                NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
            }
            val lowPriorityChannel = NotificationChannel(
                LOW_PRIORITY_CHANNEL_ID,
                "Ongoing Timer Notifications",
                NotificationManager.IMPORTANCE_LOW).apply {
                enableLights(true)
            }
            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            Timber.v("Notification channel with id of: $HIGH_PRIORITY_CHANNEL_ID created")
            notifManager.createNotificationChannel(highPriorityChannel)
            notifManager.createNotificationChannel(lowPriorityChannel)
        }
    }
}