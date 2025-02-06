package com.ssafy.tmbg.ui.schedule

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.tmbg.R
import com.ssafy.tmbg.adapter.ScheduleAdapter
import com.ssafy.tmbg.databinding.DialogAddScheduleBinding
import com.ssafy.tmbg.databinding.FragmentScheduleBinding
import com.ssafy.tmbg.data.schedule.Schedule
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * 일정 관리 화면을 담당하는 Fragment
 * 일정 목록을 보여주고 추가/수정/삭제 기능을 제공합니다.
 */
@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    // ViewBinding 객체를 nullable로 선언하여 메모리 누수 방지
    private var _binding: FragmentScheduleBinding? = null
    // Nullable이 아닌 binding 객체를 getter로 제공
    private val binding get() = _binding!!
    // 일정 목록을 표시할 RecyclerView의 어댑터
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val viewModel: ScheduleViewModel by viewModels()
    private var roomId: Long = 1L  // 기본값 1 임시로 해놓은거고 원래는 -1이 맞음

    companion object {
        private const val ARG_ROOM_ID = "room_id"

        fun newInstance(roomId: Long): ScheduleFragment {
            return ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ROOM_ID, roomId)
                }
            }
        }
    }

    /**
     * Fragment의 View를 생성하고 초기화합니다.
     * ViewBinding을 통해 레이아웃을 inflate합니다.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * View가 생성된 후 호출되며, 초기 설정을 수행합니다.
     * RecyclerView 설정과 클릭 리스너를 초기화합니다.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        
        // roomId 유효성 검사 후 요청
        if (roomId != -1L) {
            viewModel.getSchedules(roomId)
        } else {
            Toast.makeText(context, "유효하지 않은 방 ID입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * RecyclerView를 초기화하고 설정합니다.
     * 어댑터와 레이아웃 매니저를 설정합니다.
     */
    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter().apply {
            setOnEditClickListener { schedule ->
                showEditScheduleDialog(schedule)
            }
            setOnDeleteClickListener { schedule ->
                deleteSchedule(schedule)
            }
        }
        binding.scheduleRecyclerView.apply {
            adapter = scheduleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * 버튼들의 클릭 리스너를 설정합니다.
     * 뒤로가기와 일정 추가 버튼의 동작을 정의합니다.
     */
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            // 프래그먼트 스택에서 현재 프래그먼트 제거
            parentFragmentManager.popBackStack()
        }

        binding.btnAdd.setOnClickListener {
            showAddScheduleDialog()
        }
    }

    private fun setupObservers() {
        viewModel.schedules.observe(viewLifecycleOwner) { schedules ->
            scheduleAdapter.submitList(schedules)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: 로딩 표시 구현
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Fragment의 초기화 메서드
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomId = it.getLong(ARG_ROOM_ID)
        }
    }

    /**
     * 일정 추가를 위한 다이얼로그를 표시합니다.
     * 시간과 내용을 입력받아 새로운 일정을 생성합니다.
     */
    private fun showAddScheduleDialog() {
        val dialogFragment = AddScheduleDialogFragment().apply {
            setOnScheduleCreatedListener { startTime, endTime, content ->
                val schedule = Schedule.createSchedule(
                    schedulesId = System.currentTimeMillis(),
                    roomId = roomId,  // 전달받은 roomId 사용
                    startTime = parseTimeString(startTime),
                    endTime = parseTimeString(endTime),
                    content = content
                )
                scheduleAdapter.submitList(scheduleAdapter.currentList + schedule)
            }
        }
        dialogFragment.show(parentFragmentManager, AddScheduleDialogFragment.TAG)
    }

    private fun parseTimeString(timeString: String): Date {
        val (hour, minute) = timeString.split(":").map { it.toInt() }
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.time
    }

    private fun showEditScheduleDialog(schedule: Schedule) {
        val dialogFragment = AddScheduleDialogFragment().apply {
            // 기존 일정 데이터 전달
            setScheduleData(schedule)
            setOnScheduleCreatedListener { startTime, endTime, content ->
                // TODO: API 호출하여 서버에 일정 수정
                val updatedSchedule = Schedule.createSchedule(
                    schedulesId = schedule.schedulesId,
                    roomId = schedule.roomId,
                    startTime = parseTimeString(startTime),
                    endTime = parseTimeString(endTime),
                    content = content
                )
                
                val currentList = scheduleAdapter.currentList.toMutableList()
                val position = currentList.indexOfFirst { it.schedulesId == schedule.schedulesId }
                if (position != -1) {
                    currentList[position] = updatedSchedule
                    scheduleAdapter.submitList(currentList)
                }
            }
        }
        
        dialogFragment.show(parentFragmentManager, AddScheduleDialogFragment.TAG)
    }

    private fun deleteSchedule(schedule: Schedule) {
        // TODO: API 호출하여 서버에서 일정 삭제
        val currentList = scheduleAdapter.currentList.toMutableList()
        currentList.remove(schedule)
        scheduleAdapter.submitList(currentList)
    }

    /**
     * Fragment가 제거될 때 호출됩니다.
     * ViewBinding 객체를 해제하여 메모리 누수를 방지합니다.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}