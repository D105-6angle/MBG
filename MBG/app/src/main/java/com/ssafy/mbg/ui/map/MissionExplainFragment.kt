package com.ssafy.mbg.ui.map

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.mbg.R

class MissionExplainFragment : DialogFragment() {

    companion object {
        // missionId 기본값은 -1 (전달되지 않은 경우)
        fun newInstance(codeId: String, positionName: String, placeName: String, missionId: Int = -1): MissionExplainFragment {
            val fragment = MissionExplainFragment()
            val args = Bundle().apply {
                putString("codeId", codeId)
                putString("positionName", positionName)
                putString("placeName", placeName)
                putInt("missionId", missionId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.RoundedCornerDialog)
        // 이 옵션을 통해서 외부영역 터치 + 뒤로가기로 취소 안되게 함
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mission_explain, container, false)

        val quizTitle: TextView = view.findViewById(R.id.quizTitle)
        val quizText: TextView = view.findViewById(R.id.quizText)
        val btnConfirm: Button = view.findViewById(R.id.btnConfirm)
        val imageView: ImageView = view.findViewById(R.id.imageView)

        // 전달받은 인자 읽기
        val codeId = arguments?.getString("codeId") ?: "default"
        val positionName = arguments?.getString("positionName") ?: "미지정"
        val placeName = arguments?.getString("placeName") ?: ""

        // codeId에 따라 제목과 텍스트 분기 처리
        when (codeId) {
            "M001" -> {
                quizTitle.text = "문화재 미션 발생!"
                quizText.text = "$positionName 관련\n문제를 풀고\n 문화재 카드를 얻어봐"
            }
            "M002" -> {
                quizTitle.text = "랜덤 미션 발생!"
                quizText.text = "$placeName 관련\n랜덤 퀴즈를 풀고\n일화 카드를 얻어봐"
            }
            "M003" -> {
                quizTitle.text = "인증샷 미션 발생!"
                quizText.text = "인증샷을 찍어서 업로드 해주세요"
            }
            else -> {
                quizTitle.text = "퀴즈 발생!"
                quizText.text = "퀴즈를 풀어보세요"
            }
        }

        // Glide를 이용해 이미지 로드 (필요하면 codeId에 따라 분기 가능)
        Glide.with(this)
            .load(R.drawable.ggumi_quiz)
            .into(imageView)

        // "가자!" 버튼 클릭 시, 미션 코드에 따라 다른 프래그먼트를 띄움
        btnConfirm.setOnClickListener {
            when (codeId) {
                "M001" -> {
                    // M001: 문화재 퀴즈 미션 팝업
                    val missionId = arguments?.getInt("missionId") ?: 1
                    val popup = HeritageQuizMissionFragment.newInstance(missionId)
                    popup.show(parentFragmentManager, "M001Popup")
                }
                "M002" -> {
                    // M002의 경우, missionId도 전달 (없다면 기본값 0)
                    val missionId = arguments?.getInt("missionId") ?: 0
                    val popup = RandomQuizMissionFragment.newInstance(missionId, codeId, positionName, placeName)
                    popup.show(parentFragmentManager, "M002Popup")
                }
                "M003" -> {
                    // M003: 인증샷 미션 팝업
                    val missionId = arguments?.getInt("missionId") ?: 0
                    val photoFragment = PhotoMissionFragment.newInstance("M003", positionName, placeName, missionId)
                    photoFragment.show(parentFragmentManager, "PhotoMissionFragment")

                }
                else -> {
                    // 기본 퀴즈 팝업, missionId를 0으로 설정 (기본값)
                    val popup = HeritageQuizMissionFragment.newInstance(0)
                    popup.show(parentFragmentManager, "DefaultPopup")
                }
            }
            dismiss()  // MissionExplainFragment 종료
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
