package com.zaccheus.eyetimer

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.zaccheus.eyetimer.preference.TimePickerPreferenceDialog
import com.zaccheus.eyetimer.preference.TimePreference
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {

    private val DIALOG_FRAGMENT_TAG = "TimePickerDialog"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is TimePreference) {
            Timber.d("Displaying preferences dialogs ... preference is a TimePreference")
            val timepickerDialog = TimePickerPreferenceDialog.newInstance(preference.key)
            timepickerDialog.setTargetFragment(this, 0)
            timepickerDialog.show(parentFragmentManager, DIALOG_FRAGMENT_TAG)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}