package com.zaccheus.eyetimer

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.zaccheus.eyetimer.databinding.FragmentTimerBinding
import timber.log.Timber

class TimerFragment : Fragment() {

    private val vm: TimerViewModel by activityViewModels()

    private lateinit var binding: FragmentTimerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_timer, container, false)
        setHasOptionsMenu(true)

        binding.vm = vm
        binding.lifecycleOwner = this
        // When timer is clicked while stopped show a short animation when it starts again
        vm.timerState.observe(viewLifecycleOwner, {
            if (vm.timerState.value == TimerState.RUNNING) {
                onTimerStartedAnimation()
            }
        })

        return binding.root
    }

    private fun onTimerStartedAnimation() {
        val animFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        binding.testImageView.visibility = View.VISIBLE
        binding.testImageView.startAnimation(animFadeOut)
    }

    override fun onResume() {
        super.onResume()
        //Have the viewmodel check if any preferences have been changed every time this fragment
        // is reloaded
        vm.checkPrefs()
        //Check if FF and RW buttons are enabled
        handleFFRWButtons()
    }

    private fun handleFFRWButtons() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        if (prefs.getBoolean(resources.getString(R.string.pref_FF_RW_visibility_key), false)) {
            binding.buttonForward.show()
            binding.buttonRewind.show()
        } else {
            binding.buttonForward.hide()
            binding.buttonRewind.hide()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                findNavController().navigate(TimerFragmentDirections.actionTimerToSettings())
                return true
            }
            R.id.about -> {
                Toast.makeText(context, "About", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //TODO: Put this in a service and call onTaskRemoved() to tell when the app has been closed from
    // the the task pane
    // https://stackoverflow.com/questions/48346933/notification-still-appear-when-app-is-closed
    // https://stackoverflow.com/questions/19568315/how-to-handle-code-when-app-is-killed-by-swiping-in-android/26882533#26882533
    override fun onDetach() {
        super.onDetach()
        //We don't want any notifications running when the app is closed so cancel them here
        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.cancelAll()
    }
}