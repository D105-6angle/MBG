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
import androidx.navigation.fragment.navArgs
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentSignUpBinding
import com.ssafy.mbg.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val args: SignupFragmentArgs by navArgs()
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeAuthState()

        val titleAnimation = AnimationUtils.loadAnimation(context, R.anim.title_animation)
        binding.tvTitle.startAnimation(titleAnimation)

        binding.signupForm.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.form_slide_up)
        )

        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        binding.tvNicknameLabel.startAnimation(fadeIn)

        binding.nicknameInputLayout.postDelayed({
            binding.nicknameInputLayout.startAnimation(fadeIn)
        }, 200)

        binding.tvNewUserQuestion.postDelayed({
            binding.tvNewUserQuestion.startAnimation(fadeIn)
        }, 400)

        binding.btnSignUp.postDelayed({
            binding.btnSignUp.startAnimation(fadeIn)
        }, 600)

        binding.etNickname.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.input_focus))
            }
        }
    }


    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            when {
                nickname.isBlank() -> {
                    binding.nicknameInputLayout.error = "닉네임을 입력해주세요"
                }

                nickname.length < 2 -> {
                    binding.nicknameInputLayout.error = "닉네임은 2자 이상이어야 합니다"
                }

                nickname.length > 13 -> {
                    binding.nicknameInputLayout.error = "닉네임은 13자 이하여야 합니다"
                }

                else -> {
                    binding.nicknameInputLayout.error = null
                    viewModel.register(args.email, args.name, args.socialId, nickname)
                }
            }
        }
    }

    //State 감지
    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                // State 상태에 따라 분기 처리
                when (state) {
                    is AuthState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    // 회원가입 성공 시 메인 액티비티로 이동
                    is AuthState.NavigateToMain -> {
                        findNavController().navigate(
                            SignupFragmentDirections.actionSignupToOxQuiz()
                        )
//                        startActivity(Intent(requireContext(), MainActivity::class.java).apply {
//                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        })
                    }
                    // 에러 메시지 Toast로 보여주기
                    is AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}