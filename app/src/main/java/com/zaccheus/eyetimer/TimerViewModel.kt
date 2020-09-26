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