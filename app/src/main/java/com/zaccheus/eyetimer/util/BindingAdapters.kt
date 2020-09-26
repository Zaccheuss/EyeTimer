package com.zaccheus.eyetimer.util

import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

// Custom setter to convert Long to a String to show a time duration (ex: 70000L -> 01:10)
@BindingAdapter("android:text")
fun setText(view: TextView, textAsLong: Long) {

    // I think this can be ignored as we are not showing an actual date, just using SimpleDateFormat
    // to format the Long properly.
    view.text = SimpleDateFormat("mm:ss").format(Date(textAsLong))
}

// Custom setter to convert long to int for the circular progress bar
@BindingAdapter("android:progress")
fun setProgress(view: ProgressBar, progress: Long) {
    view.progress = progress.toInt()
}

@BindingAdapter("android:max")
fun setMax(view: ProgressBar, max: Long) {
    view.max = max.toInt()
}