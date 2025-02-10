package com.ssafy.mbg.ui.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.mbg.adapter.ScheduleAdapter
import com.ssafy.mbg.adapter.TeamMemberAdapter
import com.ssafy.mbg.data.mypage.dto.Schedule
import com.ssafy.mbg.databinding.FragmentTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskFragment : Fragment() {
    // ViewBinding을 위한 변수 선언
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!! // null이 아님을 보장하는 getter

    // 팀 멤버와 일정 목록을 표시할 어댑터 선언
    private val teamMemberAdapter = TeamMemberAdapter()
    private val scheduleAdapter = ScheduleAdapter()
    private var roomId : Long = 1L

    private val viewModel : TaskViewModel by viewModels()

    companion object {
        private const val ARG_ROOM_ID = "room_id"

        fun newInstance(roomId : Long) : TaskFragment {
            return TaskFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ROOM_ID, roomId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // roomId 초기화 시 예외 처리
        try {
            arguments?.let {
                roomId = it.getLong(ARG_ROOM_ID)
            }
        } catch (e: Exception) {
            Log.e("TaskFragment", "Failed to get roomId from arguments: ${e.message}")
            roomId = -1L
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return try {
            _binding = FragmentTaskBinding.inflate(inflater, container, false)
            binding.root
        } catch (e: Exception) {
            Log.e("TaskFragment", "Failed to inflate view: ${e.message}")
            View(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setupRecyclerViews()
            setupObservers()

            if (roomId != -1L) {
                viewModel.getSchedules(roomId)
            } else {
                Toast.makeText(context, "유효하지 않은 방 ID입니다.", Toast.LENGTH_SHORT).show()
            }

            loadData()
        } catch (e: Exception) {
            Log.e("TaskFragment", "Error in onViewCreated: ${e.message}")
            Toast.makeText(context, "화면 초기화 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerViews() {
        try {
            binding.teamMemberRecyclerView.apply {
                adapter = teamMemberAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            binding.scheduleList.apply {
                adapter = scheduleAdapter
                layoutManager = LinearLayoutManager(context)
            }
        } catch (e: Exception) {
            Log.e("TaskFragment", "Error setting up RecyclerViews: ${e.message}")
            Toast.makeText(context, "목록 초기화 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        try {
            viewModel.state.observe(viewLifecycleOwner) { state ->
                try {
                    when(state) {
                        is ScheduleState.Loading -> {
                            binding.progressBar?.isVisible = true
                        }
                        is ScheduleState.Success -> {
                            binding.progressBar?.isVisible = false
                            scheduleAdapter.updateSchedules(state.schedules)  // 수정된 부분
                        }
                        is ScheduleState.Error -> {
                            binding.progressBar?.isVisible = false
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                } catch (e: Exception) {
                    Log.e("TaskFragment", "Error processing state update: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("TaskFragment", "Error setting up observers: ${e.message}")
        }
    }

    private fun loadData() {
        try {
            val members = listOf("최대규", "손은주", "제갈민", "이용재", "박성민", "김병년")
            teamMemberAdapter.updateMembers(members)
        } catch (e: Exception) {
            Log.e("TaskFragment", "Error loading team members: ${e.message}")
            Toast.makeText(context, "팀원 목록을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        try {
            super.onDestroyView()
            _binding = null
        } catch (e: Exception) {
            Log.e("TaskFragment", "Error in onDestroyView: ${e.message}")
        }
    }

    // Lifecycle 관련 예외 처리 추가
    override fun onResume() {
        try {
            super.onResume()
            // 화면이 다시 보일 때 데이터 새로고침이 필요한 경우
            if (roomId != -1L) {
                viewModel.getSchedules(roomId)
            }
        } catch (e: Exception) {
            Log.e("TaskFragment", "Error in onResume: ${e.message}")
        }
    }

    override fun onPause() {
        try {
            super.onPause()
            // 필요한 정리 작업
        } catch (e: Exception) {
            Log.e("TaskFragment", "Error in onPause: ${e.message}")
        }
    }
}