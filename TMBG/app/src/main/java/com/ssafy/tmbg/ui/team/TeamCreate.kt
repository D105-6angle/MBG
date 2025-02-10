package com.ssafy.tmbg.ui.team

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import com.ssafy.tmbg.databinding.CreateTeamBinding
import com.ssafy.tmbg.data.team.dao.TeamRequest
import com.ssafy.tmbg.ui.main.AdminMainFragment
import com.ssafy.tmbg.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamCreateDialog : DialogFragment() {
    private var _binding: CreateTeamBinding? = null
    private val binding get() = _binding!!
    private val teamViewModel: TeamViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TeamViewModel의 roomId 변경을 관찰하여 MainViewModel과 동기화
        teamViewModel.roomId.observe(viewLifecycleOwner) { roomId ->
            if (roomId != -1) {
                mainViewModel.setRoomId(roomId)
            }
        }

        // TeamViewModel의 상태 관찰
        teamViewModel.team.observe(viewLifecycleOwner) { team ->
            team?.let {
                dismiss()
                (parentFragment as? AdminMainFragment)?.navigateToTeam()
            }
        }

        // 에러 메시지 관찰
        teamViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.submitButton.setOnClickListener {
            val teamName = binding.editText2.text.toString()
            val location = binding.editText1.text.toString()
            val numOfGroups = binding.editText3.text.toString().toIntOrNull() ?: 1

            Log.d("TeamCreateDialog", "버튼 클릭됨")
            Log.d("TeamCreateDialog", "teamName: $teamName, location: $location, numOfGroups: $numOfGroups")

            if (teamName.isNotEmpty()) {
                Log.d("TeamCreateDialog", "팀 생성 요청 시작")
                // 팀 생성 요청
                teamViewModel.createTeam(TeamRequest(
                    roomName = teamName,
                    location = location,
                    numOfGroups = numOfGroups
                ))
                // API 호출이 완료될 때까지 기다림 (dismiss 제거)
            } else {
                Toast.makeText(context, "팀 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그 크기 설정
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = requireContext().resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.9).toInt()
            val height = (displayMetrics.heightPixels * 0.8).toInt()

            setLayout(width, height)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}