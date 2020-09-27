package com.zaccheus.eyetimer.preference

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceDialogFragmentCompat

class TimePickerPreferenceDialog : PreferenceDialogFragmentCompat() {

    lateinit var timepicker: TimePicker

    override fun onCreateDialogView(context: Context?): View {
        timepicker = TimePicker(context)
        return timepicker
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        val minutesAfterMidnight = (preference as TimePreference).getPersistedMinutesFromMidnight()
        timepicker.setIs24HourView(true)
        timepicker.hour = minutesAfterMidnight / 60
        timepicker.minute = minutesAfterMidnight % 60
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val minutesAfterMidnight = (timepicker.hour * 60) + timepicker.minute
            (preference as TimePreference).persistMinutesFromMidnight(minutesAfterMidnight)
            preference.summary = minutesAfterMidnight.toString()
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