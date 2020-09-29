package com.zaccheus.eyetimer.preference

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceDialogFragmentCompat
import com.zaccheus.eyetimer.util.TimeConverter
import mobi.upod.timedurationpicker.*

class TimePickerPreferenceDialog : PreferenceDialogFragmentCompat() {

    lateinit var timepicker: TimeDurationPicker

    override fun onCreateDialogView(context: Context?): View {
        timepicker = TimeDurationPicker(context)
        return timepicker
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        val timeDuration = (preference as TimePreference).getPersistedTimeDuration()
        timepicker.duration = timeDuration
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val timeDuration: Long = timepicker.duration
            (preference as TimePreference).persistMinutesFromMidnight(timeDuration)
            preference.summary = TimeConverter.convertMillisToString(timeDuration)
        }
    }

    companion object {
        fun newInstance(key: String): TimePickerPreferenceDialog {
            val fragment = TimePickerPreferenceDialog()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle

            return fragment
        }
    }
}