package com.ssafy.mbg.ui.map

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.mbg.R

class PhotoMissionFragment : DialogFragment() {

    companion object {
        fun newInstance(codeId: String, positionName: String, placeName: String): PhotoMissionFragment {
            val fragment = PhotoMissionFragment()
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
        setStyle(STYLE_NORMAL, R.style.RoundedCornerDialog)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo_mission, container, false)
        val missionTitle: TextView = view.findViewById(R.id.missionTitle)
        val missionText: TextView = view.findViewById(R.id.missionText)
        val btnConfirm: Button = view.findViewById(R.id.btnConfirm)
        val imageView: ImageView = view.findViewById(R.id.imageView)

        // 인자 읽기 (인증샷 미션은 추가 인자 없이 고정 텍스트 사용)
        missionTitle.text = "인증샷 미션"
        missionText.text = "인증샷을 찍어서 업로드 해주세요."

        // 예시: 인증샷 미션 전용 이미지 로드 (R.drawable.photo_mission 리소스 필요)
        Glide.with(this)
//            .load(R.drawable.photo_mission)
            .load(R.drawable.ggumi_quiz)
            .into(imageView)

        btnConfirm.setOnClickListener {
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
