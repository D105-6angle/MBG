package com.ssafy.mbg.ui.map

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ssafy.mbg.R

class PhotoMissionResultFragment : DialogFragment() {

    companion object {
        private const val ARG_PICTURE_URL = "pictureUrl"

        fun newInstance(pictureUrl: String): PhotoMissionResultFragment {
            val fragment = PhotoMissionResultFragment()
            val args = Bundle().apply {
                putString(ARG_PICTURE_URL, pictureUrl)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var pictureUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // RoundedCornerDialog 스타일 적용
        setStyle(STYLE_NORMAL, R.style.RoundedCornerDialog)
        arguments?.let {
            pictureUrl = it.getString(ARG_PICTURE_URL)
        }
        Log.d("Map", "이거임${pictureUrl}")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_mission_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imageViewResult: ImageView = view.findViewById(R.id.imageViewResult)
        val btnClose: Button = view.findViewById(R.id.btnCloseResult)

        pictureUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_sync)
                .error(R.drawable.ic_uploadfile)
                .into(imageViewResult)
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그 너비를 화면 너비의 85%로 설정
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
