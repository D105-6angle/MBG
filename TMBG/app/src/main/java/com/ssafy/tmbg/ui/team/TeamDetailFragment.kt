package com.ssafy.tmbg.ui.team

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.ssafy.tmbg.ui.team.TeamViewModel
import com.ssafy.tmbg.data.team.dao.VerificationPhotos
import com.ssafy.tmbg.data.team.dao.GroupDetailResponse
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ssafy.tmbg.ui.SharedViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeamDetailFragment : Fragment() {
    private var _binding: FragmentTeamDetailBinding? = null
    private val binding get() = _binding!!
    private val teamViewModel: TeamViewModel by viewModels()
    private val args: TeamDetailFragmentArgs by navArgs()
    private val sharedViewModel: SharedViewModel by activityViewModels()  // MainViewModel -> SharedViewModel
    private var isDeleteMode = false
    private lateinit var memberAdapter: TeamMemberAdapter

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

        // Flow로 roomId 수집하여 상세 정보 요청
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.roomId.collect { roomId ->
                if (roomId != -1) {
                    teamViewModel.getGroupDetail(roomId, args.groupNumber)
                }
            }
        }

        setupMemberRecyclerView()
        setupPhotoRecyclerView(emptyList())
        setupPlaceRecyclerView()
    }

    private fun setupObservers() {
        teamViewModel.groupDetail.observe(viewLifecycleOwner) { groupDetail ->
            groupDetail?.let { detail ->
                // 진행률 설정
                binding.progressBar.progress = detail.progress.toInt()
                binding.progressPercent.text = "${detail.progress}%"

                // 멤버 리스트를 조장과 조원으로 분리
                val leader = detail.members.find { it.codeId == "J001" }
                val members = detail.members.filter { it.codeId != "J001" }

                // 조장 리사이클러뷰 업데이트
                binding.rvLeader.adapter = TeamMemberAdapter(
                    members = listOfNotNull(leader),
                    onDeleteClick = { userId ->
                        // Flow로 roomId 수집
                        viewLifecycleOwner.lifecycleScope.launch {
                            sharedViewModel.roomId.collect { roomId ->
                                if (roomId != -1) {
                                    teamViewModel.deleteMember(roomId, args.groupNumber, userId)
                                }
                            }
                        }
                    }
                )

                // 조원 리사이클러뷰 업데이트
                memberAdapter = TeamMemberAdapter(
                    members = detail.members.filter { it.codeId != "J001" },
                    onDeleteClick = { userId ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            sharedViewModel.roomId.collect { roomId ->
                                if (roomId != -1) {
                                    teamViewModel.deleteMember(roomId, args.groupNumber, userId)
                                }
                            }
                        }
                    }
                )
                binding.rvMembers.adapter = memberAdapter

                // 방문 장소 리사이클러뷰 업데이트
                binding.rvPlaces.adapter = TeamPlaceAdapter(detail.visitedPlaces)
            }
        }

        // 삭제 버튼 클릭 처리
        binding.btnDelete.setOnClickListener {
            isDeleteMode = !isDeleteMode
            memberAdapter.setDeleteMode(isDeleteMode)
            (binding.rvLeader.adapter as? TeamMemberAdapter)?.setDeleteMode(isDeleteMode)
            
            // 삭제 모드일 때 버튼 색상 변경
            binding.btnDelete.setColorFilter(
                if (isDeleteMode) 
                    ContextCompat.getColor(requireContext(), R.color.red)
                else 
                    ContextCompat.getColor(requireContext(), R.color.gray)
            )
        }
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

    private fun setupPhotoRecyclerView(photos: List<VerificationPhotos>) {
        val photoAdapter = TeamPhotoAdapter(
            photos = photos,
            onPhotoClick = { photo -> 
                // 사진 클릭 시 처리
                // 예: 상세 보기 다이얼로그 표시
            }
        )

        binding.rvPhotos.apply {  // photoRecyclerView -> rvPhotos로 변경
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
                }
            })
        }
    }

    private fun setupMemberRecyclerView() {
        // 조장 리사이클러뷰 설정
        binding.rvLeader.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
                }
            })
        }

        // 조원 리사이클러뷰 설정
        binding.rvMembers.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
                }
            })
        }
    }

    private fun setupPlaceRecyclerView() {
        binding.rvPlaces.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}