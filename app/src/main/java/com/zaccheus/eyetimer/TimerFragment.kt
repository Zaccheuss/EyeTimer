package com.zaccheus.eyetimer

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zaccheus.eyetimer.databinding.FragmentTimerBinding

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
        // Have the viewmodel check if any preferences have been changed every time this fragment
        // is reloaded
        vm.checkPrefs()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                Toast.makeText(context, "Settings", Toast.LENGTH_SHORT).show()
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
}