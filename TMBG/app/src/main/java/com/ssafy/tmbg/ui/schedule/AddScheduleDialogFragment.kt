package com.ssafy.tmbg.ui.schedule

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.ssafy.tmbg.R
import com.ssafy.tmbg.data.schedule.dao.Schedule
import com.ssafy.tmbg.databinding.DialogAddScheduleBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * 일정 추가/수정을 위한 다이얼로그 Fragment
 * 시간과 내용을 입력받아 일정을 생성하거나 수정합니다.
 */
class AddScheduleDialogFragment : DialogFragment() {

    private var _binding: DialogAddScheduleBinding? = null
    private val binding get() = _binding!!

    /**
     * 일정 생성/수정 완료 시 호출될 콜백
     * @param onScheduleCreated (시작시간, 종료시간, 내용) -> Unit
     */
    private var onScheduleCreated: ((String, String, String) -> Unit)? = null

    private var editingSchedule: Schedule? = null

    /**
     * 다이얼로그의 크기와 스타일을 설정합니다.
     * - 투명 배경
     * - 화면 너비의 80%
     */
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
        // ViewBinding 초기화 및 루트 뷰 반환
        _binding = DialogAddScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupCurrentTime()
        
        // 수정 모드일 경우 기존 데이터를 UI에 표시
        editingSchedule?.let { schedule ->
            binding.tvTitle.text = getString(R.string.schedule_edit_title)
            binding.btnCreate.text = getString(R.string.schedule_update)

            try {
                // 서버 형식의 시간을 파싱
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Seoul")
                }
                // 화면에 표시할 형식
                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Seoul")
                }

                // 시작 시간 파싱 및 설정
                val startDate = inputFormat.parse(schedule.startTime)
                startDate?.let {
                    val startTime = outputFormat.format(it)
                    val (hour, minute) = startTime.split(":").map { it.toInt() }
                    binding.etStartHour.setText(hour.toString())
                    binding.etStartMinute.setText(minute.toString())
                }

                // 종료 시간 파싱 및 설정
                val endDate = inputFormat.parse(schedule.endTime)
                endDate?.let {
                    val endTime = outputFormat.format(it)
                    val (hour, minute) = endTime.split(":").map { it.toInt() }
                    binding.etEndHour.setText(hour.toString())
                    binding.etEndMinute.setText(minute.toString())
                }

                // 내용 설정
                binding.etContent.setText(schedule.content)
            } catch (e: Exception) {
                Log.e("AddScheduleDialog", "시간 파싱 에러: ${e.message}")
                Log.e("AddScheduleDialog", "startTime: ${schedule.startTime}")
                Log.e("AddScheduleDialog", "endTime: ${schedule.endTime}")
                Toast.makeText(context, "시간 형식 변환 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 클릭 리스너들을 설정합니다.
     * - 시작/종료 시간 선택
     * - 생성/수정 버튼
     * - 닫기 버튼
     */
    private fun setupClickListeners() {
        // 시작 시간 선택 영역 클릭 시 TimePicker 다이얼로그 표시
        binding.layoutStartTime.setOnClickListener {
            showTimePicker(true)
        }

        // 종료 시간 선택 영역 클릭 시 TimePicker 다이얼로그 표시
        binding.layoutEndTime.setOnClickListener {
            showTimePicker(false)
        }

        // 생성/수정 버튼 클릭 시 입력값 검증 후 콜백 호출
        binding.btnCreate.setOnClickListener {
            val startHour = binding.etStartHour.text.toString()
            val startMinute = binding.etStartMinute.text.toString()
            val endHour = binding.etEndHour.text.toString()
            val endMinute = binding.etEndMinute.text.toString()
            val content = binding.etContent.text.toString()

            // 입력값이 유효한 경우에만 처리
            if (validateInputs(startHour, startMinute, endHour, endMinute, content)) {
                onScheduleCreated?.invoke("$startHour:$startMinute", "$endHour:$endMinute", content)
                dismiss()
            }
        }

        // 닫기 버튼 클릭 시 다이얼로그 종료
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    /**
     * 입력값들의 유효성을 검사합니다.
     * @return 모든 입력값이 유효하면 true
     */
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

    /**
     * 현재 시간을 기본값으로 설정합니다.
     * 시작 시간은 현재 시간, 종료 시간은 1시간 후로 설정됩니다.
     */
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

    /**
     * 시간 선택 다이얼로그를 표시합니다.
     * @param isStartTime true면 시작 시간, false면 종료 시간
     */
    private fun showTimePicker(isStartTime: Boolean) {
        // 현재 설정된 시간값을 가져옴 (없으면 0으로 설정)
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

        // TimePicker 다이얼로그 생성 및 표시
        CustomTimePickerDialog().apply {
            // 현재 설정된 시간을 초기값으로 설정
            setInitialTime(currentHour, currentMinute)
            // 시간 선택 완료 시 처리
            setOnTimeSelectedListener { hour, minute ->
                if (isStartTime) {
                    // 시작 시간인 경우
                    binding.etStartHour.setText(String.format("%02d", hour))
                    binding.etStartMinute.setText(String.format("%02d", minute))
                    
                    // 종료 시간을 시작 시간 + 1시간으로 자동 설정
                    val endHour = if (hour == 23) 0 else hour + 1
                    binding.etEndHour.setText(String.format("%02d", endHour))
                    binding.etEndMinute.setText(String.format("%02d", minute))
                } else {
                    // 종료 시간인 경우
                    binding.etEndHour.setText(String.format("%02d", hour))
                    binding.etEndMinute.setText(String.format("%02d", minute))
                }
            }
        }.show(parentFragmentManager, CustomTimePickerDialog.TAG)
    }

    /**
     * 일정 생성/수정 완료 리스너를 설정합니다.
     * @param listener 시작시간, 종료시간, 내용을 파라미터로 받는 콜백 함수
     */
    fun setOnScheduleCreatedListener(listener: (String, String, String) -> Unit) {
        onScheduleCreated = listener
    }

    /**
     * 수정할 일정 데이터를 설정합니다.
     * 이 메서드가 호출되면 다이얼로그가 수정 모드로 동작합니다.
     * @param schedule 수정할 일정 정보
     */
    fun setScheduleData(schedule: Schedule) {
        editingSchedule = schedule
    }

    /**
     * Fragment가 제거될 때 메모리 누수 방지를 위해
     * ViewBinding 객체를 해제합니다.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddScheduleDialog"
    }
} 