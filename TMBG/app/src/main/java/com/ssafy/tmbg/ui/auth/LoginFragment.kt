package com.ssafy.tmbg.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.tmbg.R
import com.ssafy.tmbg.databinding.FragmentLoginBinding
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
    }

    // 버튼 클릭 리스너 설정
    private fun setupClickListeners() {
        binding.btnKakaoLogin.setOnClickListener {
            viewModel.handleKakaoLogin()
        }
    }

    // state 상태 변화 관찰 및 처리
    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        // Show loading if needed
                    }
                    is AuthState.NeedSignUp -> {
                        val action = LoginFragmentDirections.actionLoginToSignup(
                            email = state.email,
                            name = state.name,
                            socialId = state.socialId
                        )
                        findNavController().navigate(action)
                    }
                    is AuthState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    // view 죽여~~
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}