package com.ssafy.mbg.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentSatisfactionBinding
import com.ssafy.mbg.di.UserPreferences
import com.ssafy.mbg.ui.home.HomeViewModel
import com.ssafy.mbg.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SatisfactionFragment : Fragment() {
    private var _binding: FragmentSatisfactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SatisfactionViewModel by viewModels()
    private val homeViewModel : HomeViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSatisfactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSubmitButton()
        setupBackButton()
        observeUiState()
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SatisfactionUiState.Loading -> {
                    binding.submitButton.isEnabled = false
                }
                is SatisfactionUiState.Success -> {
                    Toast.makeText(context, "만족도 조사가 제출되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is SatisfactionUiState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    binding.submitButton.isEnabled = true
                }
            }
        }
    }

    private fun setupSubmitButton() {
        binding.submitButton.setOnClickListener {
            val answer1 = when (binding.radioGroup1.checkedRadioButtonId) {
                R.id.q1_radio1 -> "매우 좋음"
                R.id.q1_radio2 -> "좋음"
                R.id.q1_radio3 -> "보통"
                R.id.q1_radio4 -> "나쁨"
                R.id.q1_radio5 -> "매우 나쁨"
                else -> null
            }

            val answer2 = when (binding.radioGroup2.checkedRadioButtonId) {
                R.id.q2_radio1 -> "매우 좋음"
                R.id.q2_radio2 -> "좋음"
                R.id.q2_radio3 -> "보통"
                R.id.q2_radio4 -> "나쁨"
                R.id.q2_radio5 -> "매우 나쁨"
                else -> null
            }

            val answer3 = when (binding.radioGroup3.checkedRadioButtonId) {
                R.id.q3_radio1 -> "매우 좋음"
                R.id.q3_radio2 -> "좋음"
                R.id.q3_radio3 -> "보통"
                R.id.q3_radio4 -> "나쁨"
                R.id.q3_radio5 -> "매우 나쁨"
                else -> null
            }

            val freeAnswer = binding.freeAnswer.text.toString()

            val userId = userPreferences.userId
            val roomId = userPreferences.roomId

            Log.d("SatisfactionFragment", "UserID: $userId, RoomID: $roomId")
//            val roomId = 1L

            if (userId == null || roomId == null) {
                Toast.makeText(context, "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (answer1 == null || answer2 == null || answer3 == null) {
                Toast.makeText(context, "모든 문항에 답변해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.submitSurvey(
                roomId = roomId,
                userId = userId,
                answer1 = answer1,
                answer2 = answer2,
                answer3 = answer3,
                answer4 = freeAnswer
            )
            homeViewModel.clearGroup()
        }

    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
