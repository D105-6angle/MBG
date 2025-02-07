package com.ssafy.tmbg.ui.schedule


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.tmbg.adapter.ScheduleAdapter
import com.ssafy.tmbg.databinding.FragmentScheduleBinding
import com.ssafy.tmbg.data.schedule.dao.Schedule
import com.ssafy.tmbg.data.schedule.dao.ScheduleRequest
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    /**
     * Fragment 인스턴스를 생성하고 roomId를 전달합니다.
     * @param roomId 방 ID
     * @return 생성된 ScheduleFragment 인스턴스
     * 
     * 동작 과정:
     * 1. 새로운 Fragment 인스턴스 생성
     * 2. arguments Bundle에 roomId 저장
     * 3. 설정된 Fragment 반환
     */
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
     * @param inflater 레이아웃을 inflate하는데 사용되는 LayoutInflater
     * @param container Fragment가 들어갈 부모 ViewGroup
     * @param savedInstanceState 이전 상태 정보
     * @return 생성된 View
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
     * 
     * 동작 과정:
     * 1. RecyclerView 설정 (어댑터, 레이아웃 매니저)
     * 2. 클릭 리스너 설정
     * 3. LiveData 옵저버 설정
     * 4. roomId 유효성 검사 후 일정 목록 로드
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
     * 
     * 동작 과정:
     * 1. 어댑터 인스턴스 생성
     * 2. 수정/삭제 클릭 리스너 설정
     * 3. RecyclerView에 어댑터 연결
     * 4. LinearLayoutManager 설정
     */
    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter().apply {
            // 수정 버튼 클릭 시 수정 다이얼로그 표시
            setOnEditClickListener { schedule ->
                showEditScheduleDialog(schedule)
            }
            // 삭제 버튼 클릭 시 일정 삭제
            setOnDeleteClickListener { schedule ->
                deleteSchedule(schedule)
            }
        }
        // RecyclerView 설정
        binding.scheduleRecyclerView.apply {
            adapter = scheduleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * 버튼들의 클릭 리스너를 설정합니다.
     * 
     * 동작 과정:
     * 1. 뒤로가기 버튼:
     *    - 클릭 시 현재 Fragment를 스택에서 제거
     * 2. 추가 버튼:
     *    - 클릭 시 일정 추가 다이얼로그 표시
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

    /**
     * ViewModel의 LiveData들을 관찰하여 UI를 업데이트합니다.
     * 
     * 동작 과정:
     * 1. schedules LiveData 관찰:
     *    - 변경 시 RecyclerView 목록 업데이트
     * 2. isLoading LiveData 관찰:
     *    - 변경 시 로딩 표시 상태 업데이트
     * 3. error LiveData 관찰:
     *    - 변경 시 에러 메시지 토스트 표시
     */
    private fun setupObservers() {
        // 일정 목록 변경 감지
        viewModel.schedules.observe(viewLifecycleOwner) { schedules ->
            scheduleAdapter.submitList(schedules)
        }

        // 로딩 상태 변경 감지
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: 로딩 표시 구현
            binding.progressBar.isVisible = isLoading
        }

        // 에러 메시지 변경 감지
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Fragment의 초기화 메서드
     * 
     * 동작 과정:
     * 1. arguments에서 roomId를 추출
     * 2. 추출한 roomId를 멤버 변수에 저장
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomId = it.getLong(ARG_ROOM_ID)
        }
    }

    /**
     * 일정 추가를 위한 다이얼로그를 표시합니다.
     * 
     * 동작 과정:
     * 1. AddScheduleDialogFragment 인스턴스 생성
     * 2. 일정 생성 리스너 설정:
     *    - 시간 문자열을 서버 형식으로 변환
     *    - ScheduleRequest 객체 생성
     *    - ViewModel을 통해 일정 생성 요청
     * 3. 다이얼로그 표시
     */
    private fun showAddScheduleDialog() {
        val dialogFragment = AddScheduleDialogFragment().apply {
            setOnScheduleCreatedListener { startTime, endTime, content ->
                // 시간 문자열을 서버 형식으로 변환
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val scheduleRequest = ScheduleRequest(
                    roomId = roomId,
                    startTime = formatter.format(parseTimeString(startTime)),
                    endTime = formatter.format(parseTimeString(endTime)),
                    content = content
                )
                viewModel.createSchedule(roomId, scheduleRequest)
            }
        }
        dialogFragment.show(parentFragmentManager, AddScheduleDialogFragment.TAG)
    }

    /**
     * 시간 문자열을 Date 객체로 변환합니다.
     * 
     * 동작 과정:
     * 1. "HH:mm" 형식의 문자열을 시간과 분으로 분리
     * 2. Calendar 인스턴스 생성
     * 3. Calendar에 시간과 분 설정
     * 4. Date 객체로 변환하여 반환
     * 
     * @param timeString "HH:mm" 형식의 시간 문자열
     * @return 변환된 Date 객체
     */
    private fun parseTimeString(timeString: String): Date {
        val (hour, minute) = timeString.split(":").map { it.toInt() }
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.time
    }

    /**
     * 일정 수정을 위한 다이얼로그를 표시합니다.
     * 
     * 동작 과정:
     * 1. AddScheduleDialogFragment 인스턴스 생성
     * 2. 기존 일정 데이터 설정
     * 3. 일정 수정 리스너 설정:
     *    - 시간 문자열을 Date 객체로 변환
     *    - 수정된 Schedule 객체 생성
     *    - ViewModel을 통해 일정 수정 요청
     * 4. 다이얼로그 표시
     * 
     * @param schedule 수정할 일정 정보
     */
    private fun showEditScheduleDialog(schedule: Schedule) {
        val dialogFragment = AddScheduleDialogFragment().apply {
            setScheduleData(schedule)
            setOnScheduleCreatedListener { startTime, endTime, content ->
                val updatedSchedule = Schedule(
                    scheduleId = schedule.scheduleId,
                    roomId = schedule.roomId,
                    startTime = startTime,
                    endTime = endTime,      // String 타입으로 직접 전달
                    content = content
                )
                viewModel.updateSchedule(roomId, schedule.scheduleId, updatedSchedule)
            }
        }
        dialogFragment.show(parentFragmentManager, AddScheduleDialogFragment.TAG)
    }

    /**
     * 일정을 삭제합니다.
     * 
     * 동작 과정:
     * 1. ViewModel을 통해 일정 삭제 요청
     * 2. 성공 시 목록에서 자동으로 제거됨 (ViewModel에서 처리)
     * 
     * @param schedule 삭제할 일정 정보
     */
    private fun deleteSchedule(schedule: Schedule) {
        viewModel.deleteSchedule(roomId, schedule.scheduleId)
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