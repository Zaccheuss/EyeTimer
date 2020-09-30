package com.zaccheus.eyetimer

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zaccheus.eyetimer.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    private val vm: TimerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentTimerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_timer, container, false)
        setHasOptionsMenu(true)

        binding.vm = vm
        binding.lifecycleOwner = this

        return binding.root
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