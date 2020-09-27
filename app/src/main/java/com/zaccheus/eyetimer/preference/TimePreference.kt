package com.zaccheus.eyetimer.preference

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.TimePicker
import androidx.preference.DialogPreference
import com.zaccheus.eyetimer.R
import kotlinx.android.synthetic.main.pref_time_picker.view.*

internal class TimePreference(context: Context) : DialogPreference(context) {

    var time = 0

    fun getHour(time: String): Int {
        val pieces = time.split(":")
        return Integer.parseInt(pieces[0])
    }

    fun getMinute(time: String): Int {
        val splitTime = time.split(":")
        return Integer.parseInt(splitTime[1])
    }

    init {
        super.setPositiveButtonText("Set")
        super.setNegativeButtonText("Cancel")
    }

    override fun getDialogLayoutResource(): Int {
        return R.layout.pref_time_picker
    }
}