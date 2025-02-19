package com.ssafy.mbg.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentLoginBinding
import com.ssafy.mbg.ui.easteregg.EasterEggDialog
import com.ssafy.mbg.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    // View 생성 완료 후 호출
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeAuthState()

        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        binding.tvTitle.startAnimation(fadeIn)
        binding.ivLogo.startAnimation(fadeIn)

        val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        binding.btnKakaoLogin.startAnimation(slideUp)
        binding.btnGoogleLogin.postDelayed({
            binding.btnGoogleLogin.startAnimation(slideUp)
        }, 100)
        binding.btnNaverLogin.postDelayed({
            binding.btnNaverLogin.startAnimation(slideUp)
        }, 200)
    }

    // 버튼 클릭 리스너 설정
    private fun setupClickListeners() {
        // 카카오 로그인 버튼 클릭
        binding.btnKakaoLogin.setOnClickListener {
            // 뷰모델 에서 카카오 로그인 함수 호출
            viewModel.handleKakaoLogin()
        }

        // 네이버 로그인 버튼 클릭
        binding.btnNaverLogin.setOnClickListener {
            // 뷰모델 에서 네이버 로그인 함수 호출
            viewModel.handleNaverLogin(requireContext())
        }
    }

    // state 상태 변화 관찰 및 처리
    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                // viewMidel에서 감지중인 state값의 변화에 따라
                when (state) {
                    // 프로그래스 바 보여주기
                    is AuthState.Loading -> {
                        // Show loading if needed
                        setLoadingState(true)
                    }
                    // 회원 가입 페이지로 이동
                    is AuthState.NeedSignUp -> {
                        // 프로그래스 바 숢기기
                        setLoadingState(false)
                        // safeArg를 통해 data 옮기기
                        val action = LoginFragmentDirections.actionLoginToSignup(
                            email = state.email,
                            name = state.name,
                            socialId = state.socialId
                        )
                        findNavController().navigate(action)
                    }
                    // 로그인 성공 시 바로 메인 엑티비티로 이동
                    is AuthState.NavigateToMain -> {
                        startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    }
                    // 에러 발생 시
                    is AuthState.Error -> {
                        // 프로그래스 바 숢기고, Toast로 에러 메시지 보여주기
                        setLoadingState(false)
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    // Loading State 설정 함수
    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            // true 이면 프로그래스 바만 보여주기
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnKakaoLogin.isEnabled = !isLoading
            btnNaverLogin.isEnabled = !isLoading
            btnGoogleLogin.isEnabled = !isLoading
        }
    }

    // view 죽여~~
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}