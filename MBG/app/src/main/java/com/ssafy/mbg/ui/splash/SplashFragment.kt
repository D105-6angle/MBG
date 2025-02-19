package com.ssafy.mbg.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentSplashBinding
import com.ssafy.mbg.ui.auth.AuthState
import com.ssafy.mbg.ui.auth.AuthViewModel
import com.ssafy.mbg.ui.main.MainActivity
import com.ssafy.mbg.util.typeWrite
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeAuthState()

        val logoAnim = AnimationUtils.loadAnimation(context, R.anim.logo_reveal)
        binding.ivLogo.startAnimation(logoAnim)

        binding.splashLogoText.postDelayed({
            binding.splashLogoText.typeWrite(getString(R.string.app_logo_name))
        }, 1000)

        binding.splashBtn.alpha = 0f
        binding.splashBtn.postDelayed({
            binding.splashBtn.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.button_reveal)
            )
        }, 2000)

        view.postDelayed({
            navigateToNextScreen()
        }, 3000)

    }

    private fun setupClickListeners() {
        binding.splashBtn.setOnClickListener {
            viewModel.checkAutoLogin()
        }
    }

    private fun navigateToNextScreen() {
        findNavController().navigate(R.id.action_splash_to_login)
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        setLoadingState(true)
                    }
                    is AuthState.NavigateToMain -> {
                        // 자동 로그인 성공 - 메인으로 이동
                        startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    }
                    is AuthState.Error -> {
                        setLoadingState(false)
                        // 자동 로그인 실패 - 로그인 화면으로 이동
                        findNavController().navigate(R.id.action_splash_to_login)
                    }
                    else -> setLoadingState(false)
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            splashBtn.isEnabled = !isLoading
            splashContent.isEnabled = !isLoading
            splashLogoText.isEnabled = !isLoading
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}