package com.zaccheus.eyetimer

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.os.CountDownTimer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.zaccheus.eyetimer.MainApplication.Companion.CHANNEL_ID
import com.zaccheus.eyetimer.TimerState.*
import com.zaccheus.eyetimer.TimerViewModel.Constants.COUNT_DOWN_INTERVAL
import com.zaccheus.eyetimer.TimerViewModel.Constants.DEFAULT_TIMER_LENGTH
import timber.log.Timber

class TimerViewModel(app: Application) : AndroidViewModel(app) {


    object Constants {
        // Used for testing purposes to make sure circular timer is working
        const val DEFAULT_TIMER_LENGTH: Long = 10000
        const val COUNT_DOWN_INTERVAL: Long = 50 //millis
    }

    private lateinit var timer: CountDownTimer
    private var app: Application

    val timeLeft = MutableLiveData<Long>()
    val timerLength = MutableLiveData<Long>()
    val timerState = MutableLiveData<TimerState>(STOPPED)

    init {
        Timber.d("TimerViewModel created")
        this.app = app
        // Since nothing is being persisted when app is closed (for now) just get the value from
        // preferences.
        timeLeft.value = getTimeFromPrefs();
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
    }

    private fun getTimeFromPrefs(): Long {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(app.applicationContext)
        val timerLengthPrefs = prefs.getLong(app.getString(R.string.pref_timer_duration_key),
            DEFAULT_TIMER_LENGTH)
        return timerLengthPrefs
    }

    fun onTimerClick() {
        if (timerState.value!! == STOPPED) {
            Timber.d("timer clicked in the STOPPED state")
            startTimer()
            timerState.value = RUNNING
        } else if(timerState.value!! == RUNNING) {
            Timber.d("timer clicked in the RUNNING state")
            timerState.equals(RUNNING)
            stopTimer()
        } else {
            Timber.e("Timer state is neither RUNNING or STOPPED, might be FINISHED")
        }
    }

    fun startTimer() {
        Timber.d("timer started")
        timer = object: CountDownTimer(timeLeft.value!!, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                Timber.v("onTick: $millisUntilFinished")
                timeLeft.value = millisUntilFinished
            }
            override fun onFinish() {
                // Make sure the time is actually zero, sometimes the onTick() method doesn't
                // set the time all the way to zero.
                timeLeft.value = 0
                timerState.value = FINISHED
                showNotification()
                Timber.v("timer finished")
            }
        }.start()
    }

    fun stopTimer() {
        timer.cancel()
        timerState.value = STOPPED
    }

    fun resetTimer() {
        timeLeft.value = timerLength.value;
        timerState.value = STOPPED
    }

    private fun showNotification() {
        val notificationManager = NotificationManagerCompat.from(app)
        val notificationBuilder = NotificationCompat.Builder(app, CHANNEL_ID)
            .setContentTitle("Timer has finished")
            .setContentText("Tap this notification to open EyeTimer")
            .setSmallIcon(R.drawable.ic_clock_white)
            .setContentIntent(setupTapIntentToOpenApp())
            .setAutoCancel(true)
        notificationManager.notify(1, notificationBuilder.build())
        Timber.v("notification sent")
    }

    private fun setupTapIntentToOpenApp(): PendingIntent {
        val myIntent = Intent(app, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(app, 1, myIntent, 0)
        return pendingIntent
    }

}