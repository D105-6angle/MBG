package com.ssafy.mbg.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentInviteCodeBinding
import androidx.navigation.fragment.findNavController

class InviteCodeFragment : DialogFragment() {

    private var _binding: FragmentInviteCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)  // TransparentDialog 스타일 사용
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.submitButton.setOnClickListener {
            val code = binding.inviteCodeInput.text.toString()
            if (code.isNotEmpty()) {
                dismiss()
                findNavController().navigate(R.id.action_homeFragment_to_roomListFragment)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                
                // 다이얼로그를 화면 중앙에 위치시킴
                setGravity(Gravity.CENTER)
                
                // 다이얼로그 크기 설정
                val params = attributes
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                attributes = params
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),  // 화면 너비의 90%
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 