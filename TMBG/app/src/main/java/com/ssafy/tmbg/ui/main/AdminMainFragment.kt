package com.ssafy.tmbg.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.mbg.ui.modal.ProfileModal
import com.ssafy.tmbg.R
import com.ssafy.tmbg.databinding.FragmentAdminMainBinding
import com.ssafy.tmbg.ui.auth.AuthViewModel
import com.ssafy.tmbg.ui.team.TeamCreateDialog
import com.ssafy.tmbg.ui.team.TeamViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminMainFragment : Fragment() {
    private var _binding: FragmentAdminMainBinding? = null
    private val binding get() = _binding!!
    private val teamViewModel: TeamViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()

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

        // TeamViewModel의 roomId 변경을 관찰하여 MainViewModel과 동기화
        teamViewModel.roomId.observe(viewLifecycleOwner) { roomId ->
            if (roomId != -1) {
                mainViewModel.setRoomId(roomId)
            }
        }
    }
    // 네비게이션 설정을 통해, 각 버튼 클릭 시 네비게이션 액션 호출
    private fun setupClickListeners() {
        binding.apply {
            // Team 버튼 클릭 시 roomId 체크
            btnTeam.setOnClickListener {
                if (mainViewModel.roomId.value != -1) {
                    mainViewModel.roomId.value?.let { roomId ->
                        Log.d("AdminMainFragment", "팀 관리 버튼 클릭 - roomId: $roomId")
                        
                        // Observer를 한 번만 설정하도록 수정
                        teamViewModel.team.removeObservers(viewLifecycleOwner)
                        teamViewModel.team.observe(viewLifecycleOwner) { team ->
                            Log.d("AdminMainFragment", "팀 정보 로드 결과: $team")
                            if (team != null) {  // null 체크를 명시적으로
                                Log.d("AdminMainFragment", "네비게이션 시도")
                                try {
                                    findNavController().navigate(R.id.action_adminMain_to_team)
                                    Log.d("AdminMainFragment", "네비게이션 성공")
                                } catch (e: Exception) {
                                    Log.e("AdminMainFragment", "네비게이션 실패: ${e.message}", e)
                                }
                            }
                        }
                        
                        // 에러 처리도 Observer 중복 제거
                        teamViewModel.error.removeObservers(viewLifecycleOwner)
                        teamViewModel.error.observe(viewLifecycleOwner) { error ->
                            Log.e("AdminMainFragment", "팀 정보 로드 에러: $error")
                            if (error.isNotEmpty()) {
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        }
                        
                        Log.d("AdminMainFragment", "getTeam 호출 시도")
                        teamViewModel.getTeam(roomId)
                    }
                } else {
                    Log.d("AdminMainFragment", "팀 생성 다이얼로그 표시")
                    // childFragmentManager 대신 parentFragmentManager 사용
                    TeamCreateDialog().show(
                        requireActivity().supportFragmentManager,  // 또는 parentFragmentManager
                        "TeamCreateDialog"
                    )
                }
            }
            // 공지사항 클릭 시 실행될 액션
            btnNotice.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_notice)
            }
            // 미션 클릭 시 실행될 액션
            btnMission.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_mission)
            }
            // 일정 관리 버튼 클릭 처리
            btnSchedule.setOnClickListener {
                if (mainViewModel.roomId.value != -1) {
                    findNavController().navigate(R.id.action_adminMain_to_schedule)
                } else {
                    Toast.makeText(context, "방을 먼저 생성해주세요", Toast.LENGTH_SHORT).show()
                }
            }
            // 보고서 클릭 시 실행될 액션
            btnReport.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_report)
            }

            // 설정 클릭 시 실행될 액션
            btnSetting.setOnClickListener{
                showProfileModal()
            }
            clearTeam.setOnClickListener{
                mainViewModel.clearRoomId()
            }
        }
    }

    private fun showProfileModal() {
        val profileModal = ProfileModal(
            context = requireContext(),
            email = "kimssafy@ssafy.com",
            name = "김싸피",
            currentNickname = "김싸피",
            onConfirm = { newNickname ->
                // 닉네임 변경 처리
                binding.progressBar.visibility = View.VISIBLE  // 로딩 표시 추가 필요
                authViewModel.updateNickname(newNickname)
            },
            onLogout = {
                authViewModel.logout()
            },
            onWithdraw = {
                authViewModel.withDraw()
            }
        )
        profileModal.show()
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