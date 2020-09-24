package com.zaccheus.eyetimer

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zaccheus.eyetimer.TimerState.*
import timber.log.Timber

class TimerViewModel : ViewModel() {

    private lateinit var timer: CountDownTimer

    val timeLeft = MutableLiveData<Long>(4000)
    val timerLength = MutableLiveData<Long>(10000)
    val timerState = MutableLiveData<TimerState>(STOPPED)

    fun startTimer() {
        Timber.v("timer started")
        timer = object: CountDownTimer(timeLeft.value!!, 50) {
            override fun onTick(millisUntilFinished: Long) {
                timerState.value = RUNNING
                Timber.v("onTick: $millisUntilFinished")
                timeLeft.value = millisUntilFinished
            }
            override fun onFinish() {
                // Make sure the time is actually zero, sometimes the onTick() method doesn't
                // set the time all the way to zero.
                timeLeft.value = 0
                timerState.value = STOPPED
                Timber.v("timer finished")
            }
        }.start()
    }

}