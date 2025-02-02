package com.ssafy.mbg.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.mbg.R
import com.ssafy.mbg.adapter.ProblemHistoryAdapter
import com.ssafy.mbg.data.ProblemHistory
import com.ssafy.mbg.databinding.FragmentPageBinding
import com.ssafy.mbg.ui.modal.ProfileModal

class PageFragment : Fragment() {
    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var problemHistoryAdapter: ProblemHistoryAdapter

    private fun initializeAdapter() {
        problemHistoryAdapter = ProblemHistoryAdapter { history ->
            val action = PageFragmentDirections.actionPageFragmentToHistoryDetailFragment(
                title = history.title,
                image = history.imageResId ?: 0,
                description = history.description ?: "",
                lastSolvedAt = history.lastSolvedAt
            )
            findNavController().navigate(action)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        initializeAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBackButton()
        setupSettingButton()
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.problemHistory.apply {
            adapter = problemHistoryAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            // 뒤로가기 처리
            activity?.onBackPressed()
        }
    }

    private fun setupSettingButton() {
        binding.settingsButton.setOnClickListener {
            showProfileModal()
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
            },
            onLogout = {
                // 로그아웃 처리
            },
            onWithdraw = {
                // 회원탈퇴 처리
            }
        )
        profileModal.show()
    }

    private fun loadData() {
        // 더미 데이터 생성 - 새로운 필드 추가
        val histories = listOf(
            ProblemHistory(
                iconResId = R.drawable.cultural_1,
                title = "철퇴",
                lastSolvedAt = "문제 풀이 날짜 : 2024-01-23",
                result = true,
                imageResId = R.drawable.cultural_1,
                description = "사찰의 한 물건으로 승려들이 큰소리를 내어 신호를 하거나 " +
                        "불교 의식을 행할 때 사용하던 법구이다. 절에서 스님들이 예불을 드릴 때나 " +
                        "의식을 행할 때도 사용되었다. 낮에는 목어를 치고 밤에는 동종을 치는 것이 " +
                        "일반적이다."
            ),
            ProblemHistory(
                iconResId = R.drawable.sajungjeon,
                title = "사정전",
                lastSolvedAt = "문제 풀이 날짜 : 2024-01-20",
                result = true,
                imageResId = R.drawable.sajungjeon,
                description = "사정전은 조선시대 궁중 건축물로, 임금이 신하들과 회의를 하거나 " +
                        "중요한 정책을 논의하던 장소입니다. 현재는 경복궁에 위치해 있으며, " +
                        "조선의 정치와 문화를 이해하는 데 중요한 유적입니다."
            ),
            ProblemHistory(
                iconResId = R.drawable.cultural_2,
                title = "뒤주",
                lastSolvedAt = "문제 풀이 날짜 : 2024-01-19",
                result = false,
                imageResId = R.drawable.cultural_2,
                description = "뒤주는 조선시대에 곡식을 보관하던 전통 가구입니다. " +
                        "주로 쌀, 보리 등의 곡물을 보관하는 데 사용되었으며, " +
                        "습기를 막기 위해 바닥을 띄워 제작되었습니다."
            )
        )
        problemHistoryAdapter.updateHistories(histories)
    }
}