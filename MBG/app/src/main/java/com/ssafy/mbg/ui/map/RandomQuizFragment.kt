package com.ssafy.mbg.ui.map

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.mbg.R

class RandomQuizFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 기존: setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert)
        // 변경: 모서리가 둥근 팝업 스타일 적용
        setStyle(STYLE_NORMAL, R.style.RoundedCornerDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_random_quiz, container, false)

        val quizTitle: TextView = view.findViewById(R.id.quizTitle)
        val btnConfirm: Button = view.findViewById(R.id.btnConfirm)
        val imageView: ImageView = view.findViewById(R.id.imageView)

        quizTitle.text = "랜덤 퀴즈 발생!"

        // Glide를 이용해 이미지 로드
        Glide.with(this)
            .load(R.drawable.ggumi_quiz) // 로컬 이미지 로드 (R.drawable.ggumi_quiz가 있다 가정)
            .into(imageView)

        btnConfirm.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그 크기 조정 (가로화면의 85% 정도로 설정)
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // RoundedCornerDialog에서 이미 배경이 지정되므로, 투명으로 해놓으면 외곽 모서리 둥근 배경만 보임
        // (아래 코드를 주석 처리하거나, 완전히 제거해도 됩니다. 여기서는 투명 적용)
        // dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
