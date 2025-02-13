package com.ssafy.mbg.ui.map

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.mbg.R

class HeritageQuizMissionFragment : DialogFragment() {

    companion object {
        fun newInstance(codeId: String, positionName: String, placeName: String): HeritageQuizMissionFragment {
            val fragment = HeritageQuizMissionFragment()
            val args = Bundle().apply {
                putString("codeId", codeId)
                putString("positionName", positionName)
                putString("placeName", placeName)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 둥근 모서리 스타일 적용 및 팝업 취소 불가 처리
        setStyle(STYLE_NORMAL, R.style.RoundedCornerDialog)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_heritage_quiz_mission, container, false)
        val missionTitle: TextView = view.findViewById(R.id.missionTitle)
        val missionText: TextView = view.findViewById(R.id.missionText)
        val btnConfirm: Button = view.findViewById(R.id.btnConfirm)
        val imageView: ImageView = view.findViewById(R.id.imageView)

        // 인자 읽기 (추가 분기가 필요하면 여기서 사용)
        val codeId = arguments?.getString("codeId") ?: "default"
        val positionName = arguments?.getString("positionName") ?: "미지정"
        // Heritage 미션에서는 placeName은 사용하지 않음

        missionTitle.text = "문화재 퀴즈 미션"
        missionText.text = "$positionName 관련 문제를 풀고 역사 카드를 획득하세요."

        // 예시: 문화재 퀴즈 전용 이미지 로드 (R.drawable.heritage_quiz 라는 리소스가 존재해야 함)
        Glide.with(this)
//            .load(R.drawable.heritage_quiz)
            .load(R.drawable.ggumi_quiz)
            .into(imageView)

        btnConfirm.setOnClickListener {
            // 확인 버튼 클릭 시 작업 후 dismiss
            dismiss()
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
}
