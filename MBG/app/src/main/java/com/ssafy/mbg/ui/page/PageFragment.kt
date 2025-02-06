package com.ssafy.mbg.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.mbg.adapter.ProblemHistoryAdapter
import com.ssafy.mbg.data.mypage.dto.MyPageDataSource
import com.ssafy.mbg.data.mypage.dto.ProblemHistory
import com.ssafy.mbg.databinding.FragmentPageBinding
import com.ssafy.mbg.ui.modal.ProfileModal

class PageFragment : Fragment() {
    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var problemHistoryAdapter: ProblemHistoryAdapter

    private fun initializeAdapter() {
        problemHistoryAdapter = ProblemHistoryAdapter { history ->
            val action = PageFragmentDirections.actionPageFragmentToHistoryDetailFragment(
                title = history.name,
                image = history.imageUrl ?: 0,
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
    private fun findProblemAll() : List<ProblemHistory> {
        return MyPageDataSource.solvedProblems
    }
    private fun loadData() {
        // 더미 데이터 생성 - 새로운 필드 추가
        val histories = findProblemAll()
        problemHistoryAdapter.updateHistories(histories)
    }
}