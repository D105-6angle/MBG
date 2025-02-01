package com.ssafy.mbg.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.mbg.adapter.ScheduleAdapter
import com.ssafy.mbg.adapter.TeamMemberAdapter
import com.ssafy.mbg.data.Schedule
import com.ssafy.mbg.databinding.FragmentTaskBinding
import com.ssafy.mbg.databinding.ItemScheduleBinding

class TaskFragment : Fragment() {
    // ViewBinding을 위한 변수 선언
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!! // null이 아님을 보장하는 getter

    // 팀 멤버와 일정 목록을 표시할 어댑터 선언
    private val teamMemberAdapter = TeamMemberAdapter()
    private val scheduleAdapter = ScheduleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Fragment의 레이아웃을 inflate하여 ViewBinding 객체 생성
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root // 생성된 뷰 반환
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기 설정
        setupRecyclerViews()

        // 더미 데이터 로드
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수를 방지하기 위해 binding 해제
    }

    // RecyclerView 초기화 함수
    private fun setupRecyclerViews() {
        binding.teamMemberRecyclerView.apply {
            adapter = teamMemberAdapter // 어댑터 설정
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) // 가로 스크롤 설정
        }

        binding.scheduleList.apply {
            adapter = scheduleAdapter // 어댑터 설정
            layoutManager = LinearLayoutManager(context) // 세로 스크롤 설정
        }
    }

    // 더미 데이터를 어댑터에 적용하는 함수
    private fun loadData() {
        // 팀 멤버 목록 생성 및 업데이트
        val members = listOf("최대규", "손은주", "제갈민", "이용재", "박성민", "김병년")
        teamMemberAdapter.updateMembers(members)

        // 일정 목록 생성 및 업데이트
        val schedules = listOf(
            Schedule(1, "광화문 탐방", "10:00", "12:00"),
            Schedule(2, "점심 시간", "12:00", "13:00"),
            Schedule(3, "경복궁 탐방", "13:00", "14:00")
        )
        scheduleAdapter.updateSchedules(schedules)
    }
}