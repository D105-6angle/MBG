package com.ssafy.tmbg.ui.team

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ssafy.tmbg.databinding.CreateTeamBinding
import com.ssafy.tmbg.data.team.dao.TeamRequest
import com.ssafy.tmbg.ui.main.AdminMainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamCreateDialog : DialogFragment() {
    private var _binding: CreateTeamBinding? = null
    private val binding get() = _binding!!
    private val teamViewModel: TeamViewModel by viewModels()

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

        // TeamViewModel의 상태 관찰
        teamViewModel.team.observe(viewLifecycleOwner) { team ->
            team?.let {
                // 팀 생성 성공 시에만 다이얼로그를 닫고 이동
                dismiss()
                (parentFragment as? AdminMainFragment)?.navigateToTeam()
            }
        }

        binding.submitButton.setOnClickListener {
            val teamName = binding.editText2.text.toString()
            val location = binding.editText1.text.toString()
            val numberOfGroups = binding.editText3.text.toString().toIntOrNull() ?: 1

            Log.d("TeamCreateDialog", "버튼 클릭됨")
            Log.d("TeamCreateDialog", "teamName: $teamName, location: $location, numberOfGroups: $numberOfGroups")

            if (teamName.isNotEmpty()) {
                Log.d("TeamCreateDialog", "팀 생성 요청 시작")
                // 팀 생성 요청
                teamViewModel.createTeam(TeamRequest(
                    roomName = teamName,
                    location = location,
                    numberOfGroups = numberOfGroups
                ))
                // dismiss와 navigate를 여기서 하지 않고 팀 생성 성공 시에 수행
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