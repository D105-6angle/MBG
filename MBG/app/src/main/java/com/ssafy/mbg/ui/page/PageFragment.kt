package com.ssafy.mbg.ui.page

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.mbg.adapter.ProblemHistoryAdapter
import com.ssafy.mbg.data.mypage.dto.MyPageDataSource
import com.ssafy.mbg.data.mypage.dto.ProblemHistory
import com.ssafy.mbg.databinding.FragmentPageBinding
import com.ssafy.mbg.ui.auth.AuthState
import com.ssafy.mbg.ui.auth.AuthViewModel
import com.ssafy.mbg.ui.modal.ProfileModal
import com.ssafy.mbg.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PageFragment : Fragment() {
    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthViewModel by viewModels()

    private lateinit var problemHistoryAdapter: ProblemHistoryAdapter

    private fun getTitle(solvedCount : Int) : String {
        return when {
            solvedCount in 0..5 -> "꿈 꾸는 도전자"
            solvedCount in 6..10 -> "꿈 많은 모험가"
            else -> "꿈이룬 탐험가"
        }

    }
    private fun initializeAdapter() {
        problemHistoryAdapter = ProblemHistoryAdapter { history ->
            val action = PageFragmentDirections.actionPageFragmentToHistoryDetailFragment(
                title = history.name,
//                image = history.imageUrl,
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapter()
        setupClickListeners()
        setupRecyclerView()
        loadData()
        observeAuthState()
    }

    private fun setupRecyclerView() {
        binding.problemHistory.apply {
            adapter = problemHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        with(binding) {
            // 설정 버튼
            settingsButton.setOnClickListener {
                showProfileModal()
            }

            // 만족도 조사 버튼
            satisfactionButton.setOnClickListener {
                findNavController().navigate(
                    PageFragmentDirections.actionPageFragmentToSatisfactionFragment()
                )
            }
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
                binding.progressBar.visibility = View.VISIBLE  // 로딩 표시 추가 필요
                authViewModel.updateNickname(newNickname)
            },
            onLogout = {
                authViewModel.logout()
            },
            onWithdraw = {
                authViewModel.withDraw()
            }
        )
        profileModal.show()
    }

    private fun findProblemAll() : List<ProblemHistory> {
        return MyPageDataSource.solvedProblems
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                binding.progressBar.visibility = View.GONE  // 로딩 표시 제거
                when (state) {
                    is AuthState.Success -> {
                        when (state.message) {
                            "닉네임이 변경되었습니다." -> {
                                Toast.makeText(context, "닉네임이 성공적으로 변경되었습니다", Toast.LENGTH_SHORT).show()
                                // UI 업데이트가 필요한 경우 여기서 처리
                                loadData()  // 데이터 새로고침
                            }
                            else -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    is AuthState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is AuthState.NavigateToLogin -> {
                        Intent(requireContext(), SplashActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(this)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadData() {
        // 더미 데이터 생성 - 새로운 필드 추가
        val histories = findProblemAll()
        problemHistoryAdapter.updateHistories(histories)

        val solvedCount = histories.size
        binding.profileTitle.text = getTitle(solvedCount)
    }
}