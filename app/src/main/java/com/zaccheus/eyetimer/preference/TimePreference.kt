package com.zaccheus.eyetimer.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.zaccheus.eyetimer.preference.TimePreference.Constants.DEFAULT_TIME
import com.zaccheus.eyetimer.util.TimeConverter

class TimePreference(context: Context?, attrs: AttributeSet?) : DialogPreference(context, attrs) {

    object Constants {
        const val DEFAULT_TIME: Long = 10000 // 10 seconds
    }

    fun getPersistedTimeDuration(): Long {
        return super.getPersistedLong(DEFAULT_TIME)
    }
    
    fun persistTimeDuration(timeDuration: Long) {
        super.persistLong(timeDuration)
        notifyChanged()
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        super.onSetInitialValue(restorePersistedValue, defaultValue)
        val time = getPersistedTimeDuration()
        summary = TimeConverter.convertMillisToString(time)
    }

}