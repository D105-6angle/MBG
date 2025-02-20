package com.ssafy.mbg.ui.map

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
import android.graphics.drawable.Drawable
import javax.inject.Inject

@AndroidEntryPoint
class RandomQuizMissionFragment : DialogFragment() {

    @Inject
    lateinit var client: okhttp3.OkHttpClient

    @Inject
    lateinit var userPreferences: com.ssafy.mbg.di.UserPreferences

    companion object {
        /**
         * missionId: 해당 미션의 ID
         * codeId, positionName, placeName: 나중에 다른 용도로 필요할 수 있으므로 그대로 전달
         */
        fun newInstance(missionId: Int, codeId: String, positionName: String, placeName: String): RandomQuizMissionFragment {
            val fragment = RandomQuizMissionFragment()
            val args = Bundle().apply {
                putInt("missionId", missionId)
                putString("codeId", codeId)
                putString("positionName", positionName)
                putString("placeName", placeName)
            }
            fragment.arguments = args
            return fragment
        }
    }

    // GET /api/random/{missionId} 응답 데이터 모델
    data class RandomQuizResponse(
        val quizId: String,
        val content: String,  // "타이틀|||문제 내용"
        val initial: String,
        val blackIconUrl: String
    )

    // POST /api/random/result/{missionId} 응답 데이터 모델
    data class RandomQuizResult(
        val result: Boolean,
        val cardImageUrl: String
    )

    private lateinit var blackIconImageView: ImageView
    private lateinit var quizTitleTextView: TextView
    private lateinit var quizContentTextView: TextView
    private lateinit var hintTextView: TextView
    private lateinit var answerEditText: EditText
    private lateinit var submitButton: Button

    private var randomQuizResponse: RandomQuizResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.RoundedCornerDialog)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        postponeEnterTransition()
        val view = inflater.inflate(R.layout.fragment_random_quiz_mission, container, false)
        blackIconImageView = view.findViewById(R.id.blackIconImageView)
        quizTitleTextView = view.findViewById(R.id.quizTitleTextView)
        quizContentTextView = view.findViewById(R.id.quizContentTextView)
        hintTextView = view.findViewById(R.id.hintTextView)
        answerEditText = view.findViewById(R.id.answerEditText)
        submitButton = view.findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val answer = answerEditText.text.toString().trim()
            if (answer.isEmpty()) {
                Toast.makeText(requireContext(), "정답을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                submitAnswer(answer)
            }
        }

        val missionId = arguments?.getInt("missionId") ?: -1
        if (missionId == -1) {
            Toast.makeText(requireContext(), "잘못된 미션 ID", Toast.LENGTH_SHORT).show()
            dismiss()
        } else {
            fetchRandomQuiz(missionId)
        }
        return view
    }

    private fun fetchRandomQuiz(missionId: Int) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("i12d106.p.ssafy.io")
            .addPathSegment("api")
            .addPathSegment("random")
            .addPathSegment("$missionId")
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("accept", "*/*")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "퀴즈 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
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
                        try {
                            val randomQuiz = Gson().fromJson(responseBody, RandomQuizResponse::class.java)
                            randomQuizResponse = randomQuiz
                            activity?.runOnUiThread {
                                updateUIWithRandomQuiz(randomQuiz)
                            }
                        } catch (e: Exception) {
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(), "퀴즈 데이터 파싱 오류", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun updateUIWithRandomQuiz(randomQuiz: RandomQuizResponse) {
        // content 형식: "타이틀|||문제 내용"
        val parts = randomQuiz.content.split("|||")
        val title = if (parts.isNotEmpty()) parts[0] else ""
        val content = if (parts.size > 1) parts[1] else ""
        quizTitleTextView.text = title
        quizContentTextView.text = content
        hintTextView.text = "힌트: ${randomQuiz.initial}"

        // blackIconUrl 로드 (HeritageQuizMissionFragment와 동일한 로직)
        Glide.with(this)
            .load(randomQuiz.blackIconUrl)
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
            .into(blackIconImageView)
    }

    private fun submitAnswer(answer: String) {
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
        // quizId는 GET 응답에서 받아온 값을 사용 (문자열이므로 int로 변환)
        val quizIdStr = randomQuizResponse?.quizId ?: "0"
        val quizId = try { quizIdStr.toInt() } catch (e: Exception) { 0 }
        val jsonBody = """
            {
              "userId": $userId,
              "answer": "$answer",
              "quizId": $quizId
            }
        """.trimIndent()
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())
        val url = "https://i12d106.p.ssafy.io/api/random/result/$missionId"
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
                        val result = Gson().fromJson(responseBody, RandomQuizResult::class.java)
                        activity?.runOnUiThread {
                            val resultFragment = QuizMissionResultFragment.newInstance(result.cardImageUrl, result.result)
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
