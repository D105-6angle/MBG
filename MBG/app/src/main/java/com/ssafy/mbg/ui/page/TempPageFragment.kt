//package com.ssafy.mbg.ui.page
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.ssafy.mbg.adapter.ProblemHistoryAdapter
//import com.ssafy.mbg.databinding.FragmentPageBinding
//import com.ssafy.mbg.di.UserPreferences
//import com.ssafy.mbg.ui.auth.AuthState
//import com.ssafy.mbg.ui.auth.AuthViewModel
//import com.ssafy.mbg.ui.modal.ProfileModal
//import com.ssafy.mbg.ui.splash.SplashActivity
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class TempPageFragment : Fragment() {
//    private var _binding: FragmentPageBinding? = null
//    private val binding get() = _binding!!
//
//    @Inject
//    lateinit var userPreferences: UserPreferences
//
//    private val authViewModel: AuthViewModel by viewModels()
//    private val myPageViewModel: MyPageViewModel by viewModels()
//
//    private lateinit var problemHistoryAdapter: ProblemHistoryAdapter
//
//    private fun getTitle(solvedCount: Int): String {
//        return when {
//            solvedCount in 0..5 -> "꿈 꾸는 도전자"
//            solvedCount in 6..10 -> "꿈 많은 모험가"
//            else -> "꿈이룬 탐험가"
//        }
//    }
//
//    private fun initializeAdapter() {
//        problemHistoryAdapter = ProblemHistoryAdapter { history ->
//            userPreferences.userId?.let { userId ->
//                viewLifecycleOwner.lifecycleScope.launch {
//                    myPageViewModel.getDetailProblem(userId = userId, cardId = history.cardId)
//                }
//            }
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentPageBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initializeAdapter()
//        setupClickListeners()
//        setupRecyclerView()
//        observeMyPageState()
//        observeAuthState()
//        loadData()
//    }
//
//    private fun setupRecyclerView() {
//        binding.problemHistory.apply {
//            adapter = problemHistoryAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//        }
//    }
//
//    private fun setupClickListeners() {
//        with(binding) {
//            settingsButton.setOnClickListener {
//                userPreferences.userId?.let { userId ->
//                    viewLifecycleOwner.lifecycleScope.launch {
//                        myPageViewModel.getUserInfo(userId)
//                    }
//                }
//            }
//
//            satisfactionButton.setOnClickListener {
//                findNavController().navigate(
//                    PageFragmentDirections.actionPageFragmentToSatisfactionFragment()
//                )
//            }
//        }
//    }
//
//    private fun showProfileModal(email: String, name: String, nickname: String) {
//        val profileModal = ProfileModal(
//            context = requireContext(),
//            email = email,
//            name = name,
//            currentNickname = nickname,
//            onConfirm = { newNickname ->
//                binding.progressBar.visibility = View.VISIBLE
//                authViewModel.updateNickname(newNickname)
//            },
//            onLogout = {
//                authViewModel.logout()
//            },
//            onWithdraw = {
//                authViewModel.withDraw()
//            }
//        )
//        profileModal.show()
//    }
//
//    private fun observeMyPageState() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            myPageViewModel.uiState.collect { state ->
//                when (state) {
//                    is MyPageState.Loading -> {
//                        binding.progressBar.visibility = View.VISIBLE
//                    }
//                    is MyPageState.Success -> {
//                        binding.progressBar.visibility = View.GONE
//
//                        state.userResponse?.let { userResponse ->
//                            showProfileModal(
//                                email = userResponse.userInfo.email,
//                                name = userResponse.userInfo.name,
//                                nickname = userResponse.userInfo.nickname
//                            )
//                        }
//
//                        state.problemResponse?.let { problemResponse ->
//                            findNavController().navigate(
//                                PageFragmentDirections.actionPageFragmentToHistoryDetailFragment(
//                                    title = problemResponse.cardName,
////                                    image = problemResponse.imageUrl,
//                                    description = problemResponse.description,
//                                    lastSolvedAt = problemResponse.lastAttemptedAt
//                                )
//                            )
//                        }
//                    }
//                    is MyPageState.Error -> {
//                        binding.progressBar.visibility = View.GONE
//                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
//                    }
//                    is MyPageState.Initial -> {
//                        binding.progressBar.visibility = View.GONE
//                    }
//                }
//            }
//        }
//    }
//
//    private fun observeAuthState() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            authViewModel.authState.collect { state ->
//                binding.progressBar.visibility = View.GONE
//                when (state) {
//                    is AuthState.Success -> {
//                        when (state.message) {
//                            "닉네임이 변경되었습니다." -> {
//                                Toast.makeText(context, "닉네임이 성공적으로 변경되었습니다", Toast.LENGTH_SHORT).show()
//                                loadData()
//                            }
//                            else -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    is AuthState.Error -> {
//                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
//                    }
//                    is AuthState.NavigateToLogin -> {
//                        Intent(requireContext(), SplashActivity::class.java).apply {
//                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            startActivity(this)
//                        }
//                    }
//                    else -> {}
//                }
//            }
//        }
//    }
//
//    private fun loadData() {
//        userPreferences.userId?.let { userId ->
//            viewLifecycleOwner.lifecycleScope.launch {
//                myPageViewModel.getUserInfo(userId)
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}