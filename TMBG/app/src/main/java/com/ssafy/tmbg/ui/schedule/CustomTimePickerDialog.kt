package com.ssafy.tmbg.ui.schedule

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ssafy.tmbg.R
import com.ssafy.tmbg.databinding.DialogTimePickerBinding
import java.util.*

class CustomTimePickerDialog : DialogFragment() {
    private var onTimeSelected: ((Int, Int) -> Unit)? = null
    private var initialHour = 0
    private var initialMinute = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogTimePickerBinding.inflate(LayoutInflater.from(context))
        
        binding.timePicker.apply {
            setIs24HourView(true)
            hour = initialHour
            minute = initialMinute
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("확인") { _, _ ->
                onTimeSelected?.invoke(
                    binding.timePicker.hour,
                    binding.timePicker.minute
                )
            }
            .setNegativeButton("취소", null)
            .create()
    }

    fun setInitialTime(hour: Int, minute: Int) {
        initialHour = hour
        initialMinute = minute
    }

    fun setOnTimeSelectedListener(listener: (Int, Int) -> Unit) {
        onTimeSelected = listener
    }

    companion object {
        const val TAG = "CustomTimePickerDialog"
    }
} 