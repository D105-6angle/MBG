package com.ssafy.mbg.ui.ox_quiz

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.DialogQuizFragmentBinding
import androidx.core.content.ContextCompat

class QuizResultDialog(
    context: Context,
    private val isCorrect: Boolean,
    private val onConfirmClick: () -> Unit
) : Dialog(context) {
    private var _binding: DialogQuizFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DialogQuizFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다이얼로그 배경을 투명하게 설정
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        with(binding) {
            if (isCorrect) {
                ivResult.setImageResource(R.drawable.quiz_char_correct)
                tvTitle.text = "정답입니다!"
                tvMessage.text = "다음 문제로 넘어갑니다"
                btnConfirm.text = "다음으로"
                btnConfirm.setBackgroundColor(ContextCompat.getColor(context, R.color.correct_color))
            } else {
                ivResult.setImageResource(R.drawable.quiz_char_wrong)
                tvTitle.text = "오답입니다!"
                tvMessage.text = "다시 한 번 생각해보세요"
                btnConfirm.text = "다시 풀기"
                btnConfirm.setBackgroundColor(ContextCompat.getColor(context, R.color.wrong_color))
            }

            btnConfirm.setOnClickListener {
                dismiss()
                onConfirmClick()
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        _binding = null
    }
}