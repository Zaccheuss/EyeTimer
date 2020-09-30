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
        const val CHANNEL_ID = "com.zaccheus.eyetimer.notificationID"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(CustomDebugTree())
        Timber.v("Application started and Timber tree planted")
        setupNotificationChannel();
    }

    private fun setupNotificationChannel() {
        // If the device version is below Oreo we don't need to create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Timer Channel",
                NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)

            }
            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            Timber.v("Notification channel with id of: $CHANNEL_ID created")
            notifManager.createNotificationChannel(channel)
        }
    }
}