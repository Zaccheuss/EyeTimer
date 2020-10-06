package com.zaccheus.eyetimer

import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.CountDownTimer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.zaccheus.eyetimer.MainApplication.Companion.HIGH_PRIORITY_CHANNEL_ID
import com.zaccheus.eyetimer.MainApplication.Companion.LOW_PRIORITY_CHANNEL_ID
import com.zaccheus.eyetimer.TimerState.*
import com.zaccheus.eyetimer.TimerViewModel.Constants.COUNT_DOWN_INTERVAL
import com.zaccheus.eyetimer.TimerViewModel.Constants.DEFAULT_TIMER_LENGTH
import com.zaccheus.eyetimer.TimerViewModel.Constants.FF_RW_INTERVAL
import com.zaccheus.eyetimer.util.convertMillisToString
import timber.log.Timber

class TimerViewModel(app: Application) : AndroidViewModel(app) {


    object Constants {
        // Used for testing purposes to make sure circular timer is working
        const val DEFAULT_TIMER_LENGTH: Long = 10000
        const val COUNT_DOWN_INTERVAL: Long = 50 //millis
        const val FF_RW_INTERVAL: Long = 5000 //millis
    }

    private lateinit var timer: CountDownTimer

    private val prefs: SharedPreferences
    private val app: Application

    val timeLeft = MutableLiveData<Long>()
    val timerLength = MutableLiveData<Long>()
    val timerState = MutableLiveData<TimerState>(STOPPED)

    init {
        Timber.d("TimerViewModel created")
        this.app = app
        prefs = PreferenceManager.getDefaultSharedPreferences(app)
        // Since nothing is being persisted when app is closed (for now) just get the value from
        // preferences.
        timeLeft.value = getTimeFromPrefs()
        timerLength.value = getTimeFromPrefs()
    }

    fun checkPrefs() {
        // Check if the current timer length is different from the timer length stored in
        // preferences. If so, the timer length in preferences has been changed and the timer
        // should be reset
        if (getTimeFromPrefs() != timerLength.value) {
            Timber.d("Timer length has been changed in preferences")
            timerLength.value = getTimeFromPrefs()
            // Don't try to access the timer if it hasn't been initialized yet. Happens when
            // TimerFragment is first created.
            if (this::timer.isInitialized) {
                stopTimer()
                resetTimer()
            }
        }
        // Check if the user turned off ongoing notifications. Cancel all notifications if so.
        if (!getOngoingNotifToggleFromPrefs()) {
            cancelAllNotifications()
        } else {
            showNotification(buildPersistentNotification())
        }
    }

    private fun getTimeFromPrefs(): Long {
        val timerLengthPref
                = prefs.getLong(app.getString(R.string.pref_timer_duration_key),
            DEFAULT_TIMER_LENGTH)
        return timerLengthPref
    }

    private fun getOngoingNotifToggleFromPrefs(): Boolean {
        val notifTogglePref
                = prefs.getBoolean(app.getString(R.string.pref_ongoing_notification_toggle_key),
            true)
        return notifTogglePref
    }

    fun fastForward() {
        if (timeLeft.value!! > 50) { // Check if we are already finished (or very close to finish)
            if (timerState.value == RUNNING) { // Need to stop the timer to fast forward
                Timber.d("Fast forward button clicked while timer is running")
                stopTimer()
                if (timeLeft.value!! - FF_RW_INTERVAL < 0) {
                    Timber.d("Fast forward past the end of the timer, setting to 0")
                    timeLeft.value = 1 // setting to 1 lets the timer finish and call timer.onFinish()
                } else {
                    timeLeft.value = timeLeft.value!! - FF_RW_INTERVAL
                }
                startTimer()
            } else {
                if (timeLeft.value!! - FF_RW_INTERVAL < 0) {
                    Timber.d("Fast forward past the end of the timer, setting to 0")
                    timeLeft.value = 0
                    //Initialize the timer here in case somebody FF to the end of the of the timer
                    // the first time the app is opened. I probably want to initialize the timer in
                    // the init block but this works for now
                    startTimer()
                    timer.onFinish()
                } else {
                    timeLeft.value = timeLeft.value!! - FF_RW_INTERVAL
                }
            }
        } else {
            Timber.d("can't fast forward, timer is finished")
        }
    }
    fun fastRewind() {
        if (timerState.value == RUNNING) {
            Timber.d("Fast rewind button clicked while timer is running")
            stopTimer()
            timeLeft.value = timeLeft.value!! + FF_RW_INTERVAL
            //Check if timer will rewind past the initial timer length
            if (timeLeft.value!! + FF_RW_INTERVAL > timerLength.value!!) {
                Timber.d("Rewind past the beginning of the timer, setting to timer length")
                timeLeft.value = timerLength.value
            } else {
                timeLeft.value = timeLeft.value!! + FF_RW_INTERVAL
            }
            startTimer()
        } else {
            if (timeLeft.value!! + FF_RW_INTERVAL > timerLength.value!!) {
                Timber.d("Rewind past the beginning of the timer, setting to timer length")
                timeLeft.value = timerLength.value
            } else {
                timeLeft.value = timeLeft.value!! + FF_RW_INTERVAL
            }
        }
    }

    fun onTimerClick() {
        if (timerState.value!! == STOPPED) {
            Timber.d("timer clicked in the STOPPED state")
            startTimer()
        } else if(timerState.value!! == RUNNING) {
            Timber.d("timer clicked in the RUNNING state")
            timerState.equals(RUNNING)
            stopTimer()
        } else {
            Timber.e("Timer state is neither RUNNING or STOPPED, might be FINISHED")
        }
    }

    private fun startTimer() {
        Timber.d("timer created with ${timeLeft.value} millis")
        timer = object: CountDownTimer(timeLeft.value!!, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                Timber.v("onTick: $millisUntilFinished")
                timeLeft.value = millisUntilFinished
                if (getOngoingNotifToggleFromPrefs()) {
                    showNotification(buildPersistentNotification())
                }
            }
            override fun onFinish() {
                // Make sure the time is actually zero, sometimes the onTick() method doesn't
                // set the time all the way to zero.
                timeLeft.value = 0
                timerState.value = FINISHED
                // Not cancelling the ongoing notification sometimes causes timer end notification
                // to not appear
                cancelAllNotifications()
                showNotification(buildTimerEndNotification())
                Timber.v("timer finished")
            }
        }.start()
        Timber.d("timer started")
        timerState.value = RUNNING
    }

    private fun stopTimer() {
        timer.cancel()
        timerState.value = STOPPED
    }

    fun resetTimer() {
        timeLeft.value = timerLength.value;
        timerState.value = STOPPED
    }

    private fun buildTimerEndNotification() : Notification {
        val notificationBuilder = NotificationCompat.Builder(app, HIGH_PRIORITY_CHANNEL_ID)
            .setContentTitle("Timer has finished")
            .setContentText("Tap this notification to open EyeTimer")
            .setSmallIcon(R.drawable.ic_clock_white)
            .setContentIntent(setupTapIntentToOpenApp())
            .setAutoCancel(true)
        return notificationBuilder.build()
    }

    private fun buildPersistentNotification() : Notification {
        val notificationBuilder = NotificationCompat.Builder(app, LOW_PRIORITY_CHANNEL_ID)
            .setContentTitle("Time Left")
            .setContentText(convertMillisToString(timeLeft.value!!))
            .setSmallIcon(R.drawable.ic_clock_white)
            .setContentIntent(setupTapIntentToOpenApp())
            .setOnlyAlertOnce(true)
            .setOngoing(true)
        return notificationBuilder.build()
    }

    private fun showNotification(notification: Notification) {
        val notificationManager = NotificationManagerCompat.from(app)
        notificationManager.notify(1, notification)
    }

    private fun cancelAllNotifications() {
        val notificationManager = NotificationManagerCompat.from(app)
        notificationManager.cancelAll()
    }

    private fun setupTapIntentToOpenApp(): PendingIntent {
        val myIntent = Intent(app, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(app, 1, myIntent, 0)
        return pendingIntent
    }

}