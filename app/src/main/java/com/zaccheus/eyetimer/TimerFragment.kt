package com.zaccheus.eyetimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.zaccheus.eyetimer.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)

        val vm: TimerViewModel by viewModels()
        binding.vm = vm
        binding.lifecycleOwner = this

        return binding.root
    }
}