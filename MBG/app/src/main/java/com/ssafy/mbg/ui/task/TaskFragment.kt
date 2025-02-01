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
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val teamMemberAdapter = TeamMemberAdapter()
    private val scheduleAdapter = ScheduleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViews() {
        binding.teamMemberRecyclerView.apply {  // XML의 ID와 일치하는지 확인
            adapter = teamMemberAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.scheduleList.apply {
            adapter = scheduleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadData() {
        val members = listOf("최대규", "손은주", "제갈민", "이용재", "박성민", "김병년")
        teamMemberAdapter.updateMembers(members)

        val schedules = listOf(
            Schedule(1, "광화문 탐방", "10:00", "12:00"),
            Schedule(2, "점심 시간", "12:00", "13:00"),
            Schedule(3, "경복궁 탐방", "13:00", "14:00")
        )
        scheduleAdapter.updateSchedules(schedules)
    }
}