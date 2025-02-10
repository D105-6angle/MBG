package com.ssafy.tmbg.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    private fun initRecyclerView() {
        teamAdapter = TeamAdapter(
            onTeamClick = { groupNumber ->
                viewModel.team.value?.let { team ->
                    // team.teacher.groups에서 해당 그룹 정보를 찾아서 전달
                    val group = team.teacher.groups.find { it.groupNo.toInt() == groupNumber }
                    val action = TeamFragmentDirections.actionTeamToTeamDetail(
                        groupNumber = groupNumber,
                        memberCount = group?.memberCount?.toInt() ?: 0
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
    }

    private fun setupObservers() {
        viewModel.team.observe(viewLifecycleOwner) { team ->
            teamAdapter.updateData(team.numOfGroups.toInt())
            // fragment_team.xml의 btnShareCode에 초대 코드 표시
            binding.btnShareCode.text = "초대 코드: ${team.inviteCode}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // 메모리 누수 방지
    }
}