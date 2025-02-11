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
import android.widget.ArrayAdapter
import com.ssafy.tmbg.R

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
                Log.d("TeamCreateDialog", "Team created successfully: $team")
                Log.d("TeamCreateDialog", "parentFragment: $parentFragment")
                dismiss()
                val adminMainFragment = parentFragment as? AdminMainFragment
                Log.d("TeamCreateDialog", "adminMainFragment: $adminMainFragment")
                adminMainFragment?.navigateToTeam() ?: run {
                    Log.e("TeamCreateDialog", "Failed to cast parentFragment to AdminMainFragment")
                }
            }
        }

        // 에러 메시지 관찰
        teamViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // 문화재 목록 설정
        val locations = arrayOf(
            "경복궁",
            "인동향교",
            // 더 많은 문화재 추가
        )
        
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            locations
        ).apply {
            // 필터 설정
            setNotifyOnChange(true)
        }
        
        binding.locationDropdown.apply {
            setAdapter(adapter)
            threshold = 1  // 1글자 입력부터 필터링 시작
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    showDropDown()  // 포커스 받으면 드롭다운 표시
                }
            }
        }

        binding.submitButton.setOnClickListener {
            val teamName = binding.editText2.text.toString()
            val location = binding.locationDropdown.text.toString()  // 변경된 부분
            val numOfGroups = binding.editText3.text.toString().toIntOrNull() ?: 1

            if (teamName.isNotEmpty() && location.isNotEmpty()) {
                teamViewModel.createTeam(TeamRequest(
                    roomName = teamName,
                    location = location,
                    numOfGroups = numOfGroups
                ))
            } else {
                Toast.makeText(context, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그 크기 설정
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = requireContext().resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.8).toInt()  // 90% -> 80%
            val height = (displayMetrics.heightPixels * 0.6).toInt()  // 80% -> 60%

            setLayout(width, height)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}