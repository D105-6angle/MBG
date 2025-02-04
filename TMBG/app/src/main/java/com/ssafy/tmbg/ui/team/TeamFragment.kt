package com.ssafy.tmbg.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.tmbg.data.team.TeamDataSource
import com.ssafy.tmbg.databinding.FragmentTeamBinding
import com.ssafy.tmbg.ui.adapter.TeamAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamFragment : Fragment(){
    // 1. binding을 nullable로 선언하여 안전하게 관리
    private var _binding: FragmentTeamBinding? = null
    private val binding get() = _binding!!
    private lateinit var teamAdapter: TeamAdapter


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
    }

    private fun initRecyclerView() {
        teamAdapter = TeamAdapter(TeamDataSource.dummyTeams) { team ->
            // 팀 클릭 시
            val action = TeamFragmentDirections.actionTeamToTeamDetail(team.id)  // Safe Args 사용
            findNavController().navigate(action)
        }

        binding.recyclerView.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)  // 성능 최적화
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // 메모리 누수 방지
    }
}