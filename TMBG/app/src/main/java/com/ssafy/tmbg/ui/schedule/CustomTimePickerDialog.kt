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

/**
 * 시간 선택을 위한 커스텀 다이얼로그
 * 시간과 분을 선택할 수 있는 TimePicker를 제공합니다.
 */
class CustomTimePickerDialog : DialogFragment() {
    /**
     * 시간 선택 완료 시 호출될 콜백
     * (시간, 분) -> Unit
     */
    private var onTimeSelected: ((Int, Int) -> Unit)? = null
    
    // 초기 시간 값 저장
    private var initialHour = 0
    private var initialMinute = 0

    /**
     * 다이얼로그 뷰를 생성하고 설정합니다.
     * - TimePicker를 24시간 형식으로 설정
     * - 초기 시간 값 설정
     * - 확인/취소 버튼 설정
     * @return 설정이 완료된 AlertDialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogTimePickerBinding.inflate(LayoutInflater.from(context))
        
        // TimePicker 초기 설정
        binding.timePicker.apply {
            // 24시간 형식으로 표시
            setIs24HourView(true)
            // 초기 시간 설정
            hour = initialHour
            minute = initialMinute
        }

        // AlertDialog 생성 및 설정
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("확인") { _, _ ->
                // 확인 버튼 클릭 시 선택된 시간 전달
                onTimeSelected?.invoke(
                    binding.timePicker.hour,
                    binding.timePicker.minute
                )
            }
            .setNegativeButton("취소", null)
            .create()
    }

    /**
     * 초기 시간을 설정합니다.
     * @param hour 시간 (0-23)
     * @param minute 분 (0-59)
     */
    fun setInitialTime(hour: Int, minute: Int) {
        initialHour = hour
        initialMinute = minute
    }

    /**
     * 시간 선택 완료 리스너를 설정합니다.
     * @param listener 선택된 (시간, 분)을 전달받는 콜백 함수
     */
    fun setOnTimeSelectedListener(listener: (Int, Int) -> Unit) {
        onTimeSelected = listener
    }

    companion object {
        const val TAG = "CustomTimePickerDialog"
    }
} 