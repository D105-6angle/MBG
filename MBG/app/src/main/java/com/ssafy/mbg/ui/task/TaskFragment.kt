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
import com.ssafy.mbg.databinding.FragmentTaskBinding
import com.ssafy.mbg.di.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val teamMemberAdapter by lazy { TeamMemberAdapter() }
    private val scheduleAdapter by lazy { ScheduleAdapter() }

    @Inject
    lateinit var userPreferences: UserPreferences

    private val viewModel: TaskViewModel by viewModels()

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
        setupObservers()
        viewModel.getSchedules()
    }

    private fun setupRecyclerViews() {
        binding.teamMemberRecyclerView.apply {
            adapter = teamMemberAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.scheduleList.apply {
            adapter = scheduleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupObservers() {
        // 상태 관찰
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ScheduleState.Loading -> {
                    binding.progressBar?.isVisible = true
                }
                is ScheduleState.Success -> {
                    binding.progressBar?.isVisible = false
                    scheduleAdapter.updateSchedules(state.schedules)
                }
                is ScheduleState.Error -> {
                    binding.progressBar?.isVisible = false
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    scheduleAdapter.updateSchedules(emptyList())
                }
                else -> {}
            }
        }

        // 팀 멤버 관찰
        viewModel.teamMembers.observe(viewLifecycleOwner) { members ->
            Log.d("TaskFragment", "TeamMembers observed: ${members.size}")
            teamMemberAdapter.updateMembers(members)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSchedules()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}