package com.ssafy.mbg.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentSatisfactionBinding

class SatisfactionFragment : Fragment() {
    private var _binding: FragmentSatisfactionBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setupSubmitButton() {
        binding.submitButton.setOnClickListener {
            // 라디오 버튼 선택 확인
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

            val freeAnswer = binding.freeAnswer.text.toString()

            // 필수 응답 체크
            if (answer1 == null || answer2 == null) {
                Toast.makeText(context, "모든 문항에 답변해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: 응답 데이터 처리 (API 호출 등)

            // 제출 완료 후 이전 화면으로 돌아가기
            findNavController().navigateUp()
        }
    }

    private fun hideBottomNavigation() {
        activity?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activity?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE
        _binding = null
    }
}