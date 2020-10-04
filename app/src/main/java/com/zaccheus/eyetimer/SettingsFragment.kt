package com.zaccheus.eyetimer

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.zaccheus.eyetimer.preference.TimePickerPreferenceDialog
import com.zaccheus.eyetimer.preference.TimePreference
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat() {

    private val TIMER_DIALOG_FRAGMENT_TAG = "TimePickerDialog"

    private lateinit var TIMER_DURATION_KEY: String
    private lateinit var ONGOING_NOTIF_TOGGLE_KEY: String
    private lateinit var preferenceListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey)

        // Set values here so we have access to application context
        TIMER_DURATION_KEY = resources.getString(R.string.pref_timer_duration_key)
        ONGOING_NOTIF_TOGGLE_KEY = resources.getString(R.string.pref_ongoing_notification_toggle_key)

        setPreferenceSummaries()
        preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            setPreferenceSummaries()
        }

        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    private fun setPreferenceSummaries() {
        val switchPref =
            this.findPreference<SwitchPreferenceCompat>(ONGOING_NOTIF_TOGGLE_KEY)!!
        if (switchPref.isChecked) {
            switchPref.summary = "Show a notification while the timer is running"
        } else {
            switchPref.summary = "Don't show anything while the timer is running"
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference != null) {
            when (preference.key) {
                TIMER_DURATION_KEY ->
                    onTimerDurationClick(preference)
                else ->
                    super.onDisplayPreferenceDialog(preference)
            }
        } else {
            Timber.e("preference clicked was null")
        }
    }

    private fun onTimerDurationClick(preference: Preference) {
        Timber.d("Displaying preferences dialogs ... preference is a TimePreference")
        val timepickerDialog = TimePickerPreferenceDialog.newInstance(preference.key)
        timepickerDialog.setTargetFragment(this, 0)
        timepickerDialog.show(parentFragmentManager, TIMER_DIALOG_FRAGMENT_TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }
}