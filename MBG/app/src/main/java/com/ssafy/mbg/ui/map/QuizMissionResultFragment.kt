package com.ssafy.mbg.ui.map

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.mbg.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizMissionResultFragment : DialogFragment() {

    companion object {
        fun newInstance(objectImageUrl: String, correct: Boolean): QuizMissionResultFragment {
            val fragment = QuizMissionResultFragment()
            val args = Bundle().apply {
                putString("objectImageUrl", objectImageUrl)
                putBoolean("correct", correct)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var resultImageView: ImageView
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // RoundedCornerDialog 스타일 적용 및 팝업 취소 불가
        setStyle(STYLE_NORMAL, R.style.RoundedCornerDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quiz_mission_result, container, false)
        resultImageView = view.findViewById(R.id.resultImageView)
        resultTextView = view.findViewById(R.id.resultTextView)

        val objectImageUrl = arguments?.getString("objectImageUrl") ?: ""
        val correct = arguments?.getBoolean("correct") ?: false

        if (correct) {
            resultTextView.text = "정답입니다!"
            Glide.with(this)
                .load(objectImageUrl)
                .error(R.drawable.cultural_1)
                .into(resultImageView)
        } else {
            resultTextView.text = "오답입니다."
            Glide.with(this)
                .load(R.drawable.wrong_answer_ggumi)
                .into(resultImageView)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    // 방법 2: parentFragmentManager 사용하여 "refreshMission" 이벤트 전달
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parentFragmentManager.setFragmentResult("refreshMission", Bundle())
    }
}
