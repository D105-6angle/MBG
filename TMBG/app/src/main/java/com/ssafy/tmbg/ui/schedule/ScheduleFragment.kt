package com.ssafy.tmbg.ui.schedule


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.tmbg.adapter.ScheduleAdapter
import com.ssafy.tmbg.databinding.FragmentScheduleBinding
import com.ssafy.tmbg.data.schedule.dao.Schedule
import com.ssafy.tmbg.data.schedule.dao.ScheduleRequest
import com.ssafy.tmbg.ui.SharedViewModel
import com.ssafy.tmbg.ui.team.TeamViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 일정 관리 화면을 담당하는 Fragment
 * 일정 목록을 보여주고 추가/수정/삭제 기능을 제공합니다.
 */
@AndroidEntryPoint
class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val viewModel: ScheduleViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var roomId: Int = -1
    private val teamViewModel: TeamViewModel by viewModels()

    companion object {
        private const val ARG_ROOM_ID = "room_id"

        fun newInstance(roomId: Int): ScheduleFragment {
            return ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ROOM_ID, roomId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.roomId.collect { roomId ->
                if (roomId != -1) {
                    viewModel.getSchedules(roomId)
                } else {
                    Toast.makeText(context, "방을 먼저 생성해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

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

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnAdd.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.roomId.collect { roomId ->
                    showAddScheduleDialog(roomId)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.schedules.observe(viewLifecycleOwner) { schedules ->
            scheduleAdapter.setScheduleList(schedules)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomId = it.getInt(ARG_ROOM_ID)
        }
    }

    private fun showAddScheduleDialog(roomId: Int) {
        val dialogFragment = AddScheduleDialogFragment().apply {
            setOnScheduleCreatedListener { startTime, endTime, content ->
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

    private fun parseTimeString(timeString: String): Date {
        val (hour, minute) = timeString.split(":").map { it.toInt() }
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.time
    }

    private fun showEditScheduleDialog(schedule: Schedule) {
        val dialogFragment = AddScheduleDialogFragment().apply {
            setScheduleData(schedule)
            setOnScheduleCreatedListener { startTime, endTime, content ->
                val today = Calendar.getInstance().apply {
                    timeZone = TimeZone.getTimeZone("Asia/Seoul")
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    sharedViewModel.roomId.collect { currentRoomId ->
                        Log.d("ScheduleFragment", "Edit schedule - currentRoomId: $currentRoomId, schedule.roomId: ${schedule.roomId}")
                    }
                }

                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                formatter.timeZone = TimeZone.getTimeZone("Asia/Seoul")

                val (startHour, startMinute) = startTime.split(":").map { it.toInt() }
                val startDate = today.clone() as Calendar
                startDate.set(Calendar.HOUR_OF_DAY, startHour)
                startDate.set(Calendar.MINUTE, startMinute)

                val (endHour, endMinute) = endTime.split(":").map { it.toInt() }
                val endDate = today.clone() as Calendar
                endDate.set(Calendar.HOUR_OF_DAY, endHour)
                endDate.set(Calendar.MINUTE, endMinute)

                val updatedSchedule = Schedule(
                    scheduleId = schedule.scheduleId,
                    roomId = schedule.roomId,
                    startTime = formatter.format(startDate.time),
                    endTime = formatter.format(endDate.time),
                    content = content
                )

                Log.d("ScheduleFragment", "Updating schedule with: $updatedSchedule")
                viewModel.updateSchedule(schedule.roomId, schedule.scheduleId, updatedSchedule)
            }
        }
        dialogFragment.show(parentFragmentManager, AddScheduleDialogFragment.TAG)
    }

    private fun deleteSchedule(schedule: Schedule) {
        viewModel.deleteSchedule(roomId, schedule.scheduleId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}