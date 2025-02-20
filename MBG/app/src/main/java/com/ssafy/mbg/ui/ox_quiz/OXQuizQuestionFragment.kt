package com.ssafy.mbg.ui.ox_quiz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.mbg.data.oxquiz.QuizData
import com.ssafy.mbg.data.oxquiz.QuizQuestion
import com.ssafy.mbg.databinding.FragmentQuizQuestionBinding
import com.ssafy.mbg.di.QuizPreferences
import com.ssafy.mbg.ui.main.MainActivity

class OXQuizQuestionFragment : Fragment() {
    private var _binding: FragmentQuizQuestionBinding? = null
    private val binding get() = _binding!!

    private var currentQuestion = 0
    private val totalQuestions = QuizData.questions.size
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupQuestion()
        setupClickListeners()
    }

    private fun setupQuestion() {
        val question = QuizData.questions[currentQuestion]
        binding.tvQuestionNumber.text = "안전 수칙 OX 퀴즈\n문제 ${currentQuestion + 1}"
        binding.tvQuestion.text = question.question
    }

    private fun setupClickListeners() {
        binding.cvButtonO.setOnClickListener { checkAnswer(true) }
        binding.cvButtonX.setOnClickListener { checkAnswer(false) }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val currentQuizQuestion = QuizData.questions[currentQuestion]

        if (userAnswer == currentQuizQuestion.correctAnswer) {
            showCorrectAnswerDialog()
        } else {
            showWrongAnswerDialog()
        }
    }

    private fun showCorrectAnswerDialog() {
        QuizResultDialog(
            context = requireContext(),
            isCorrect = true
        ) {
            if (currentQuestion < totalQuestions - 1) {
                currentQuestion++
                setupQuestion()
            } else {
                completeQuiz()
            }
        }.show()
    }

    private fun showWrongAnswerDialog() {
        QuizResultDialog(
            context = requireContext(),
            isCorrect = false
        ) {
            // 틀렸을 때는 아무것도 하지 않고 다시 풀게 함
        }.show()
    }

    private fun completeQuiz() {
        // 퀴즈 완료 상태 저장
        QuizPreferences.setQuizCompleted(requireContext(), true)

        // SplashActivity 종료하고 MainActivity로 이동
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}