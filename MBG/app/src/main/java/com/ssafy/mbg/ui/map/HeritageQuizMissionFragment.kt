package com.ssafy.mbg.ui.map

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.ssafy.mbg.R
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import android.graphics.Color
import javax.inject.Inject

@AndroidEntryPoint
class HeritageQuizMissionFragment : DialogFragment() {

    @Inject
    lateinit var client: okhttp3.OkHttpClient

    @Inject
    lateinit var userPreferences: com.ssafy.mbg.di.UserPreferences

    companion object {
        fun newInstance(missionId: Int): HeritageQuizMissionFragment {
            val fragment = HeritageQuizMissionFragment()
            val args = Bundle().apply {
                putInt("missionId", missionId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    // 데이터 모델: HeritageQuizResponse
    data class HeritageQuizResponse(
        val problemId: Int,
        val heritageName: String,
        val imageUrl: String,      // 퀴즈 이미지
        val description: String,
        val objectImageUrl: String, // 정답시 보여줄 이미지
        val content: String,
        val choices: List<String>,
        val answer: String
    )

    // 데이터 모델: QuizResult (정답 제출 후 응답)
    data class QuizResult(
        val objectImageUrl: String,
        val correct: Boolean
    )

    private lateinit var quizImageView: ImageView
    private lateinit var contentTextView: TextView
    private lateinit var choicesContainer: LinearLayout
    private lateinit var btnSubmit: Button

    // 사용자가 선택한 답변 저장
    private var selectedAnswer: String? = null
    private var quizResponse: HeritageQuizResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // RoundedCornerDialog 스타일 적용 및 팝업 취소 불가
        setStyle(STYLE_NORMAL, R.style.RoundedCornerDialog)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // postponeEnterTransition로 UI 완성 후 표시
        postponeEnterTransition()
        val view = inflater.inflate(R.layout.fragment_heritage_quiz_mission, container, false)

        quizImageView = view.findViewById(R.id.quizImageView)
        contentTextView = view.findViewById(R.id.contentTextView)
        choicesContainer = view.findViewById(R.id.choicesContainer)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            if (selectedAnswer == null) {
                Toast.makeText(requireContext(), "답변을 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                submitAnswer()
            }
        }

        // missionId를 인자로 받아 퀴즈 요청
        val missionId = arguments?.getInt("missionId") ?: -1
        if (missionId == -1) {
            Toast.makeText(requireContext(), "잘못된 미션 ID", Toast.LENGTH_SHORT).show()
            dismiss()
        } else {
            fetchHeritageQuiz(missionId)
        }

        return view
    }

    private fun fetchHeritageQuiz(missionId: Int) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("i12d106.p.ssafy.io")
            .addPathSegment("api")
            .addPathSegment("missions")
            .addPathSegment("quiz")
            .addPathSegment("heritage")
            .addPathSegment("$missionId")
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "문제 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        activity?.runOnUiThread {
                            Toast.makeText(requireContext(), "오류: ${response.code}", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                        return
                    }
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val gson = Gson()
                        val quiz = gson.fromJson(responseBody, HeritageQuizResponse::class.java)
                        quizResponse = quiz
                        activity?.runOnUiThread {
                            updateUIWithQuiz(quiz)
                        }
                    }
                }
            }
        })
    }

    private fun updateUIWithQuiz(quiz: HeritageQuizResponse) {
        Glide.with(this)
            .load(quiz.imageUrl)
            .error(R.drawable.cultural_1)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(quizImageView)

        contentTextView.text = quiz.content

        choicesContainer.removeAllViews()
        for (choice in quiz.choices) {
            val btnChoice = Button(requireContext())
            btnChoice.text = choice
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 8, 0, 8)
            btnChoice.layoutParams = lp

            btnChoice.setOnClickListener {
                selectedAnswer = choice
                updateChoiceButtonsHighlight()
            }
            choicesContainer.addView(btnChoice)
        }
    }

    private fun updateChoiceButtonsHighlight() {
        for (i in 0 until choicesContainer.childCount) {
            val child = choicesContainer.getChildAt(i)
            if (child is Button) {
                if (child.text == selectedAnswer) {
                    child.setBackgroundColor(Color.YELLOW)
                } else {
                    child.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }

    private fun submitAnswer() {
        val missionId = arguments?.getInt("missionId") ?: -1
        if (missionId == -1) {
            Toast.makeText(requireContext(), "잘못된 미션 ID", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = userPreferences.userId
        if (userId == null) {
            Toast.makeText(requireContext(), "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val jsonBody = """
            {
              "userId": $userId,
              "answers": "${selectedAnswer}"
            }
        """.trimIndent()
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())
        val url = "https://i12d106.p.ssafy.io/api/missions/quiz/heritage/$missionId"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("accept", "*/*")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "정답 제출 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        activity?.runOnUiThread {
                            Toast.makeText(requireContext(), "오류: ${response.code}", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val gson = Gson()
                        val result = gson.fromJson(responseBody, QuizResult::class.java)
                        activity?.runOnUiThread {
                            val resultFragment = QuizMissionResultFragment.newInstance(result.objectImageUrl, result.correct)
                            resultFragment.show(parentFragmentManager, "QuizResultFragment")
                            dismiss()
                        }
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
