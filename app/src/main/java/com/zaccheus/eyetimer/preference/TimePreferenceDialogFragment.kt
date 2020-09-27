package com.zaccheus.eyetimer.preference

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceDialogFragmentCompat
import com.zaccheus.eyetimer.R


class TimePreferenceDialogFragment : PreferenceDialogFragmentCompat() {

    private val timePicker = requireView().findViewById<TimePicker>(R.id.pref_time_picker)

    fun newInstance(key: String?): TimePreferenceDialogFragment? {
        val fragment = TimePreferenceDialogFragment()
        val b = Bundle(1)
        b.putString(ARG_KEY, key)
        fragment.arguments = b
        return fragment
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val hours = timePicker.hour
            val minutes = timePicker.minute
            val minutesAfterMidnight = (hours * 60) + minutes

            // Get related preference and save the value
            if (preference is TimePreference) {
                val pref = preference as TimePreference
                if (pref.callChangeListener(minutesAfterMidnight)) {
                    pref.time = minutesAfterMidnight
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

        // Get the time from the related preference
        var minutesAfterMidnight: Int? = null
        if (preference is TimePreference) {
            minutesAfterMidnight = (preference as TimePreference).time
        }

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            val hours = minutesAfterMidnight / 60
            val minutes = minutesAfterMidnight % 60

            timePicker.hour = hours
            timePicker.minute = minutes
        }
    }
}