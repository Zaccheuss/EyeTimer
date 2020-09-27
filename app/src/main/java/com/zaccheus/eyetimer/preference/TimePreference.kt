package com.zaccheus.eyetimer.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference

class TimePreference(context: Context?, attrs: AttributeSet?) : DialogPreference(context, attrs) {

    fun getPersistedMinutesFromMidnight(): Int {
        return super.getPersistedInt(DEFAULT_MINUTES_FROM_MIDNIGHT)
    }

    fun persistMinutesFromMidnight(minutesFromMidnight: Int) {
        super.persistInt(minutesFromMidnight)
        notifyChanged()
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        super.onSetInitialValue(restorePersistedValue, defaultValue)
        summary = getPersistedMinutesFromMidnight().toString()
    }

    companion object {
        private const val DEFAULT_HOUR = 9
        const val DEFAULT_MINUTES_FROM_MIDNIGHT = DEFAULT_HOUR * 60
    }
}