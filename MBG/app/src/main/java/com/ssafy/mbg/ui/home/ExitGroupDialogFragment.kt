package com.ssafy.mbg.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ssafy.mbg.databinding.DialogExitGroupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExitGroupDialogFragment : DialogFragment() {
    private var _binding: DialogExitGroupBinding? = null
    private val binding get() = _binding!!

    var onConfirmClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogExitGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.confirmButton.setOnClickListener {
            onConfirmClick?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)  // 배경을 투명하게
                
                // 다이얼로그가 보여질 때 크기 설정
                setLayout(
                    (resources.displayMetrics.widthPixels * 0.8).toInt(),  // 화면 너비의 80%
                    (resources.displayMetrics.heightPixels * 0.3).toInt()  // 화면 높이의 30%
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.8).toInt(),
                (resources.displayMetrics.heightPixels * 0.3).toInt()
            )
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
} 