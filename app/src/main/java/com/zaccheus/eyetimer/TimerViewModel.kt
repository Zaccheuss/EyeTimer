package com.zaccheus.eyetimer

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.zaccheus.eyetimer.TimerState.*
import com.zaccheus.eyetimer.TimerViewModel.Constants.DEFAULT_TIMER_LENGTH
import com.zaccheus.eyetimer.TimerViewModel.Constants.DEFAULT_TIME_LEFT
import timber.log.Timber

class TimerViewModel(app: Application) : AndroidViewModel(app) {


    object Constants {
        // Used for testing purposes to make sure circular timer is working
        const val DEFAULT_TIME_LEFT: Long = 4000
        const val DEFAULT_TIMER_LENGTH: Long = 10000
    }

    private lateinit var timer: CountDownTimer
    private var app: Application

    val timeLeft = MutableLiveData<Long>(DEFAULT_TIME_LEFT)
    val timerLength = MutableLiveData<Long>(DEFAULT_TIMER_LENGTH)
    val timerState = MutableLiveData<TimerState>(STOPPED)

    init {
        Timber.d("TimerViewModel created")
        this.app = app
    }

    fun checkPrefs() {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(app.applicationContext)
        val timerLengthPrefs = prefs.getLong(app.getString(R.string.pref_timer_duration_key),
            DEFAULT_TIMER_LENGTH)
        Timber.d("Time preference in viewmodel: $timerLengthPrefs")

        // Check if the current timer length is different from the timer length stored in
        // preferences. If so, the timer length in preferences has been changed and the timer
        // should be reset
        if (timerLengthPrefs != timerLength.value) {
            Timber.d("Timer length has been changed in preferences")
            timerLength.value = timerLengthPrefs
            timeLeft.value = timerLengthPrefs
        }

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
        timer = object: CountDownTimer(timeLeft.value!!, 50) {
            override fun onTick(millisUntilFinished: Long) {
                Timber.v("onTick: $millisUntilFinished")
                timeLeft.value = millisUntilFinished
            }
            override fun onFinish() {
                // Make sure the time is actually zero, sometimes the onTick() method doesn't
                // set the time all the way to zero.
                timeLeft.value = 0
                timerState.value = FINISHED
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

}