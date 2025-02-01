package com.ssafy.mbg.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentCardPopupBinding

class CardPopupFragment : DialogFragment() {
    private var _binding: FragmentCardPopupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        arguments?.getInt(ARG_IMAGE_RES_ID)?.let { imageResId ->
            binding.popupCardImage.setImageResource(imageResId)
        }

        // 팝업 외부 클릭 시 닫기
        dialog?.window?.decorView?.setOnClickListener {
            dismiss()
        }
        // 팝업 내부 클릭은 이벤트 소비 (외부 클릭 이벤트가 발생하지 않도록)
        binding.root.setOnClickListener { }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IMAGE_RES_ID = "image_res_id"

        fun newInstance(imageResId: Int): CardPopupFragment {
            return CardPopupFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_IMAGE_RES_ID, imageResId)
                }
            }
        }
    }
}
