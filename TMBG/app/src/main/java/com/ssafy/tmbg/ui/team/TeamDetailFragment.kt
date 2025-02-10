package com.ssafy.tmbg.ui.team

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.R
import com.ssafy.tmbg.data.team.dao.Team
import com.ssafy.tmbg.databinding.FragmentTeamDetailBinding
import com.ssafy.tmbg.adapter.TeamMemberAdapter
import com.ssafy.tmbg.adapter.TeamPhotoAdapter
import com.ssafy.tmbg.adapter.TeamPlaceAdapter

class TeamDetailFragment : Fragment() {
    private var _binding: FragmentTeamDetailBinding? = null
    private val binding get() = _binding!!

    private val args: TeamDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()

        // 리사이클러뷰 설정
        setupMemberRecyclerView()
        setupPhotoRecyclerView()
        setupPlaceRecyclerView()
    }

    private fun setupUI() {
        binding.apply {
            // 툴바 타이틀 설정 (그룹 번호)
            toolbarTitle.text = "${args.groupNumber}조"

            // 뒤로가기 버튼
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupObservers() {
    }

    private fun setupPhotoRecyclerView() {
    }

    private fun setupMemberRecyclerView() {
    }

    private fun setupPlaceRecyclerView() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}