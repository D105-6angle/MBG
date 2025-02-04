package com.ssafy.tmbg.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ssafy.tmbg.R
import com.ssafy.tmbg.databinding.FragmentAdminMainBinding
import com.ssafy.tmbg.ui.team.TeamCreateDialog

class AdminMainFragment : Fragment() {
    private var _binding: FragmentAdminMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }
    // 네비게이션 설정을 통해, 각 버튼 클릭 시 네비게이션 액션 호출
    private fun setupClickListeners() {
        binding.apply {
            // Team의 경우 먼저 팀 생성 모달창이 보여줘야 함
            btnTeam.setOnClickListener {
                TeamCreateDialog().show(childFragmentManager, "TeamCreateDialog")
            }
            // 공지사항 클릭 시 실행될 액션
            btnNotice.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_notice)
            }
            // 미션 클릭 시 실행될 액션
            btnMission.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_mission)
            }
            // 스케쥴 클릭 시 실행될 액션
            btnSchedule.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_schedule)
            }
            // 보고서 클릭 시 실행될 액션
            btnReport.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_report)
            }
        }
    }

    fun navigateToTeam() {
        // 모달창에서 쓸 액션 이 곳에서 정의(메인 화면이 가장 최상단이기 때문)
        findNavController().navigate(R.id.action_adminMain_to_team)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}