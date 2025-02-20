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
import com.ssafy.mbg.R
import com.ssafy.mbg.adapter.ProblemHistoryAdapter
import com.ssafy.mbg.databinding.FragmentPageBinding
import com.ssafy.mbg.di.UserPreferences
import com.ssafy.mbg.ui.auth.AuthState
import com.ssafy.mbg.ui.auth.AuthViewModel
import com.ssafy.mbg.ui.modal.ProfileModal
import com.ssafy.mbg.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PageFragment : Fragment() {
    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthViewModel by viewModels()
    private val myPageViewModel : MyPageViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

    private lateinit var problemHistoryAdapter: ProblemHistoryAdapter

    private var currentEmail : String = ""
    private var currentName : String = ""
    private var currentNickname : String = ""

    private fun getTitle(solvedCount : Int) : String {
        return when {
            solvedCount in 0..5 -> "꿈 꾸는 도전자"
            solvedCount in 6..10 -> "꿈 많은 모험가"
            else -> "꿈이룬 탐험가"
        }

    }

    private fun getProfileImage(solvedCount : Int): Int {
        return when {
            solvedCount in 0..5 -> R.drawable.profile_example
            solvedCount in 6..10 -> R.drawable.profile_intermediate
            else -> R.drawable.profile_advanced
        }
    }
    private fun initializeAdapter() {
        problemHistoryAdapter = ProblemHistoryAdapter { history ->
            val action = PageFragmentDirections.actionPageFragmentToHistoryDetailFragment(
                cardId = history.cardId
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
        observeAuthState()
        observeMyPageState()
        loadData()
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
            email = currentEmail,
            name = currentName,
            currentNickname = currentNickname,
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

    private fun observeMyPageState() {
        viewLifecycleOwner.lifecycleScope.launch {
            myPageViewModel.uiState.collect { state ->
                when (state) {
                    is MyPageState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MyPageState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        state.profileResponse?.let { profile ->
                            problemHistoryAdapter.updateHistories(profile.attemptedProblems)

                            val solvedCount = profile.attemptedProblems.size


                            binding.profileTitle.text = getTitle(solvedCount)
                            binding.profileImage.setImageResource(getProfileImage(solvedCount))

                            profile.userInfo.let { userInfo ->
                                currentEmail = userInfo.email
                                currentName = userInfo.name
                                currentNickname = userInfo.nickname

                                binding.profileName.text = userInfo.nickname
                            }
                        }
                    }
                    is MyPageState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreferences.userId?.let { userId ->
                val roomIdString = userPreferences.roomId
                myPageViewModel.getProfile(userId, roomIdString)
            } ?: run {
                Toast.makeText(context, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                Intent(requireContext(), SplashActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(this)
                }
            }
        }
    }
}