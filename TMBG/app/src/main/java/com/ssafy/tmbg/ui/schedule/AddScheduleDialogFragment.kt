package com.ssafy.tmbg.ui.schedule

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.ssafy.tmbg.R
import com.ssafy.tmbg.data.schedule.Schedule
import com.ssafy.tmbg.databinding.DialogAddScheduleBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * 일정 추가를 위한 다이얼로그를 구현한 DialogFragment
 * 시간과 내용을 입력받아 일정을 생성합니다.
 */
class AddScheduleDialogFragment : DialogFragment() {

    private var _binding: DialogAddScheduleBinding? = null
    private val binding get() = _binding!!

    // 일정 생성 완료 시 호출될 콜백
    // (시작시간, 종료시간, 내용) 형태로 데이터 전달
    private var onScheduleCreated: ((String, String, String) -> Unit)? = null

    private var editingSchedule: Schedule? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                requestFeature(Window.FEATURE_NO_TITLE)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            setOnShowListener {
                val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
                window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupCurrentTime()
        
        // 수정 모드일 경우 기존 데이터 표시
        editingSchedule?.let { schedule ->
            val timeFormat = SimpleDateFormat("HH", Locale.getDefault())
            val minuteFormat = SimpleDateFormat("mm", Locale.getDefault())
            
            binding.tvTitle.text = getString(R.string.schedule_edit_title)
            binding.btnCreate.text = getString(R.string.schedule_update)
            
            binding.etStartHour.setText(timeFormat.format(schedule.startTime))
            binding.etStartMinute.setText(minuteFormat.format(schedule.startTime))
            binding.etEndHour.setText(timeFormat.format(schedule.endTime))
            binding.etEndMinute.setText(minuteFormat.format(schedule.endTime))
            binding.etContent.setText(schedule.content)
        }
    }

    private fun setupClickListeners() {
        // 시작 시간 선택
        binding.layoutStartTime.setOnClickListener {
            showTimePicker(true)
        }

        // 종료 시간 선택
        binding.layoutEndTime.setOnClickListener {
            showTimePicker(false)
        }

        binding.btnCreate.setOnClickListener {
            val startHour = binding.etStartHour.text.toString()
            val startMinute = binding.etStartMinute.text.toString()
            val endHour = binding.etEndHour.text.toString()
            val endMinute = binding.etEndMinute.text.toString()
            val content = binding.etContent.text.toString()

            if (validateInputs(startHour, startMinute, endHour, endMinute, content)) {
                onScheduleCreated?.invoke("$startHour:$startMinute", "$endHour:$endMinute", content)
                dismiss()
            }
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun validateInputs(
        startHour: String,
        startMinute: String,
        endHour: String,
        endMinute: String,
        content: String
    ): Boolean {
        if (startHour.isBlank() || startMinute.isBlank() || 
            endHour.isBlank() || endMinute.isBlank() || content.isBlank()) {
            Toast.makeText(context, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
            return false
        }

        // 시작 시간이 종료 시간보다 늦은 경우 체크
        val startTime = startHour.toInt() * 60 + startMinute.toInt()
        val endTime = endHour.toInt() * 60 + endMinute.toInt()
        if (startTime >= endTime) {
            Toast.makeText(context, "종료 시간은 시작 시간보다 늦어야 합니다", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun setupCurrentTime() {
        val calendar = Calendar.getInstance()
        
        // 현재 시간을 표시
        val dateFormat = SimpleDateFormat("yyyy년 M월 d일 (E) a h:mm", Locale.KOREAN)
        binding.tvCurrentTime.text = dateFormat.format(calendar.time)
        
        // 시작 시간을 현재 시간으로 설정
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        
        binding.etStartHour.setText(String.format("%02d", hour))
        binding.etStartMinute.setText(String.format("%02d", minute))
        
        // 종료 시간은 현재 시간 + 1시간으로 설정
        calendar.add(Calendar.HOUR_OF_DAY, 1)
        val endHour = calendar.get(Calendar.HOUR_OF_DAY)
        
        binding.etEndHour.setText(String.format("%02d", endHour))
        binding.etEndMinute.setText(String.format("%02d", minute))
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val currentHour = if (isStartTime) {
            binding.etStartHour.text.toString().toIntOrNull() ?: 0
        } else {
            binding.etEndHour.text.toString().toIntOrNull() ?: 0
        }
        
        val currentMinute = if (isStartTime) {
            binding.etStartMinute.text.toString().toIntOrNull() ?: 0
        } else {
            binding.etEndMinute.text.toString().toIntOrNull() ?: 0
        }

        CustomTimePickerDialog().apply {
            setInitialTime(currentHour, currentMinute)
            setOnTimeSelectedListener { hour, minute ->
                if (isStartTime) {
                    // 시작 시간 설정
                    binding.etStartHour.setText(String.format("%02d", hour))
                    binding.etStartMinute.setText(String.format("%02d", minute))
                    
                    // 종료 시간을 시작 시간 + 1시간으로 자동 설정
                    val endHour = if (hour == 23) 0 else hour + 1
                    binding.etEndHour.setText(String.format("%02d", endHour))
                    binding.etEndMinute.setText(String.format("%02d", minute))
                } else {
                    // 종료 시간만 설정
                    binding.etEndHour.setText(String.format("%02d", hour))
                    binding.etEndMinute.setText(String.format("%02d", minute))
                }
            }
        }.show(parentFragmentManager, CustomTimePickerDialog.TAG)
    }

    fun setOnScheduleCreatedListener(listener: (String, String, String) -> Unit) {
        onScheduleCreated = listener
    }

    fun setScheduleData(schedule: Schedule) {
        editingSchedule = schedule
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddScheduleDialog"
    }
} 