package com.ssafy.tmbg.ui.team

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.tmbg.databinding.FragmentTeamBinding
import com.ssafy.tmbg.adapter.TeamAdapter
import com.ssafy.tmbg.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamFragment : Fragment() {
    // 1. binding을 nullable로 선언하여 안전하게 관리
    private var _binding: FragmentTeamBinding? = null
    private val binding get() = _binding!!
    private lateinit var teamAdapter: TeamAdapter
    private val viewModel: TeamViewModel by viewModels()
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 2. View가 완전히 생성된 후 RecyclerView 초기화
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setupObservers()
        setupClickListeners()
        
        // MainViewModel의 roomId 관찰
        sharedViewModel.roomId.observe(viewLifecycleOwner) { roomId ->
            if (roomId != -1) {
                viewModel.getTeam(roomId)
            }
        }
    }

    private fun initRecyclerView() {
        teamAdapter = TeamAdapter(
            onTeamClick = { groupNumber ->
                viewModel.team.value?.let { team ->
                    // groups 리스트에서 해당 그룹 찾기
                    val group = team.groups.find { it.groupNo == groupNumber }
                    val action = TeamFragmentDirections.actionTeamToTeamDetail(
                        groupNumber = groupNumber,
                        memberCount = group?.memberCount ?: 0
                    )
                    findNavController().navigate(action)
                }
            }
        )

        binding.recyclerView.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)  // 성능 최적화
        }
    }

    private fun setupClickListeners() {
        binding.btnShare.setOnClickListener {
            viewModel.shareInviteCode(requireContext())
        }

        // 그룹 추가 버튼 클릭 리스너에 로그 추가
        binding.btnAdd.setOnClickListener {
            Log.d("TeamFragment", "그룹 추가 버튼 클릭")
            sharedViewModel.roomId.value?.let { roomId ->
                Log.d("TeamFragment", "그룹 추가 시도 - roomId: $roomId")
                viewModel.addGroup(roomId)
            } ?: run {
                Log.e("TeamFragment", "roomId가 null입니다")
            }
        }
    }

    private fun setupObservers() {
        viewModel.team.observe(viewLifecycleOwner) { team ->
            team?.let {
                teamAdapter.updateData(it.numOfGroups.toInt())
                binding.btnShareCode.text = "초대 코드: ${it.inviteCode}"
                binding.tvRoomName.text = it.roomName
            }
        }

        // 에러 메시지 관찰
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // 메모리 누수 방지
    }
}