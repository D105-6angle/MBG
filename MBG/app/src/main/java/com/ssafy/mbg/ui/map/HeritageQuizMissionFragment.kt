package com.ssafy.mbg.ui.map

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.ssafy.mbg.R
//import okhttp3.*
import java.io.IOException
import android.graphics.Color
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response

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
                putInt("missionId", missionId)  // missionId를 Int로 전달
            }
            fragment.arguments = args
            return fragment
        }
    }

    // 데이터 모델: HeritageQuizResponse
    data class HeritageQuizResponse(
        val problemId: Int,
        val heritageName: String,
        val imageUrl: String,
        val description: String,
        val objectImageUrl: String,
        val content: String,
        val choices: List<String>,
        val answer: String
    )

    private lateinit var quizImageView: ImageView
    private lateinit var contentTextView: TextView
    private lateinit var choicesContainer: LinearLayout
    private lateinit var btnSubmit: Button

    // 사용자가 선택한 답변을 저장하는 변수
    private var selectedAnswer: String? = null
    private var quizResponse: HeritageQuizResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // RoundedCornerDialog 스타일 적용 및 팝업 취소 불가 설정
        setStyle(STYLE_NORMAL, R.style.RoundedCornerDialog)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_heritage_quiz_mission, container, false)

        quizImageView = view.findViewById(R.id.quizImageView)
        contentTextView = view.findViewById(R.id.contentTextView)
        choicesContainer = view.findViewById(R.id.choicesContainer)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            if (selectedAnswer == null) {
                Toast.makeText(requireContext(), "답변을 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 정답 여부 확인
                if (selectedAnswer == quizResponse?.answer) {
                    Toast.makeText(requireContext(), "정답입니다!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "오답입니다. 정답은 ${quizResponse?.answer} 입니다.", Toast.LENGTH_SHORT).show()
                }
                dismiss()
            }
        }

        // missionId를 인자로 받아 quiz를 요청
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
//        val url = "https://i12d106.p.ssafy.io/api/missions/quiz/heritage/$missionId"

        val url = HttpUrl.Builder()
            .scheme("https")
            .host("i12d106.p.ssafy.io")
            .addPathSegment("api")
            .addPathSegment("missions")
            .addPathSegment("quiz")
            .addPathSegment("heritage")
            .addPathSegment("$missionId")
            .build()

//        Toast.makeText(requireContext(), "확인: $url", Toast.LENGTH_SHORT).show()

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
        // 이미지 로드, 만약 실패하면 대체 이미지(cultural_1.png) 사용
        Glide.with(this)
            .load(quiz.objectImageUrl)
            .error(R.drawable.cultural_1) // 이미지가 없으면 대체 이미지 사용
            .into(quizImageView)

        // 문제 내용 설정
        contentTextView.text = quiz.content

        // 선택지 버튼 동적 생성
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
                    child.setBackgroundColor(Color.YELLOW) // 선택된 버튼을 강조
                } else {
                    child.setBackgroundColor(Color.TRANSPARENT) // 다른 버튼은 기본 색상
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
