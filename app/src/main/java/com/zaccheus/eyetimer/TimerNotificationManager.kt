package com.zaccheus.eyetimer

import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zaccheus.eyetimer.util.convertMillisToString

class TimerNotificationManager(val app: Application) {

    fun timerEndNotification() {
        val notificationBuilder = NotificationCompat.Builder(app,
            MainApplication.HIGH_PRIORITY_CHANNEL_ID
        )
            .setContentTitle("Timer has finished")
            .setContentText("Tap this notification to open EyeTimer")
            .setSmallIcon(R.drawable.ic_clock_white)
            .setContentIntent(setupTapIntentToOpenApp())
            .setAutoCancel(true)
        showNotification(notificationBuilder.build())
    }

    fun persistentNotification(timeLeft: Long) {
        val notificationBuilder = NotificationCompat.Builder(app,
            MainApplication.LOW_PRIORITY_CHANNEL_ID
        )
            .setContentTitle("Time Left")
            .setContentText(convertMillisToString(timeLeft))
            .setSmallIcon(R.drawable.ic_clock_white)
            .setContentIntent(setupTapIntentToOpenApp())
            .setOnlyAlertOnce(true)
            .setOngoing(true)
        showNotification(notificationBuilder.build())
    }

    fun cancelAllNotifications() {
        val notificationManager = NotificationManagerCompat.from(app)
        notificationManager.cancelAll()
    }

    private fun showNotification(notification: Notification) {
        val notificationManager = NotificationManagerCompat.from(app)
        notificationManager.notify(1, notification)
    }

    private fun setupTapIntentToOpenApp(): PendingIntent {
        val myIntent = Intent(app, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(app, 1, myIntent, 0)
        return pendingIntent
    }
}