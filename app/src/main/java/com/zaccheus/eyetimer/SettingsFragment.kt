package com.zaccheus.eyetimer

import android.os.Bundle
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.preference.PreferenceFragmentCompat
import com.zaccheus.eyetimer.preference.TimePreference
import com.zaccheus.eyetimer.preference.TimePreferenceDialogFragment

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        // Check if we are using our custom preference
        var dialogFragment: PreferenceDialogFragmentCompat? = null
        if (preference is TimePreference) {
            dialogFragment = TimePreferenceDialogFragment().newInstance(preference.key)
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(this.requireFragmentManager(), "android.support.preference.PreferenceFragment.DIALOG")

            //else
            super.onDisplayPreferenceDialog(preference)
        }

        super.onDisplayPreferenceDialog(preference)
    }
}