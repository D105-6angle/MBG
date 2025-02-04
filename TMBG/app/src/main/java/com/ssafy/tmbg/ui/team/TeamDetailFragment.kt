package com.ssafy.tmbg.ui.team

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.R
import com.ssafy.tmbg.data.team.Team
import com.ssafy.tmbg.data.team.TeamDataSource
import com.ssafy.tmbg.databinding.FragmentTeamDetailBinding
import com.ssafy.tmbg.ui.adapter.TeamMemberAdapter
import com.ssafy.tmbg.ui.adapter.TeamPhotoAdapter
import com.ssafy.tmbg.ui.adapter.TeamPlaceAdapter

class TeamDetailFragment : Fragment() {
    private var _binding: FragmentTeamDetailBinding? = null
    private val binding get() = _binding!!

    private val args: TeamDetailFragmentArgs by navArgs()
    private lateinit var team: Team

    private lateinit var photoAdapter: TeamPhotoAdapter
    private lateinit var memberAdapter: TeamMemberAdapter
    private lateinit var placeAdapter: TeamPlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    // 프래먼트가 렌더링 됐을 때 실행 될 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        team = findTeamById(args.teamId)
        // 리사이클러 뷰 세팅
        setupMemberRecyclerView()
        setupPhotoRecyclerView()
        setupPlaceRecyclerView()
        setupUI()
    }

    private fun findTeamById(teamId: String): Team {
        return TeamDataSource.dummyTeams.find { it.id == teamId }
            ?: throw IllegalArgumentException("Team not found with id: $teamId")
    }

    private fun setupUI() {
        // 진행률 설정
        binding.apply {
            progressBar.progress = team.progress
            progressPercent.text = "${team.progress}%"

            // 툴바 타이틀 설정
            toolbarTitle.text = "${team.number}조"

            // 뒤로가기 버튼
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupPhotoRecyclerView() {
        photoAdapter = TeamPhotoAdapter(
            photos = team.photos,
            onPhotoClick = { photo ->
                // 사진 클릭 시 처리 (예: 큰 이미지로 보기)
            }
        )

        binding.rvPhotos.apply {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            // 아이템 간격 설정
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = resources.getDimensionPixelSize(R.dimen.item_spacing)
                }
            })
        }
    }

    private fun setupMemberRecyclerView() {
        memberAdapter = TeamMemberAdapter(team.members)  // team은 전달받은 데이터

        binding.rvMembers.apply {
            adapter = memberAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)  // 가로 스크롤
            // 아이템 간격 설정
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = resources.getDimensionPixelSize(R.dimen.item_spacing)
                }
            })
        }
    }

    private fun setupPlaceRecyclerView() {
        placeAdapter = TeamPlaceAdapter(team.visitedPlaces)

        binding.rvPlaces.apply {
            adapter = placeAdapter
            layoutManager = LinearLayoutManager(context)  // 세로 스크롤
            // 아이템 간격 설정
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = resources.getDimensionPixelSize(R.dimen.item_spacing)
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}