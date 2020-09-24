package com.zaccheus.eyetimer

import android.app.Application
import com.zaccheus.eyetimer.util.CustomDebugTree
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(CustomDebugTree())
        Timber.v("Application started and Timber tree planted")
    }
}