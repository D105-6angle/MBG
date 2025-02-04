package com.ssafy.tmbg.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ssafy.tmbg.databinding.CreateTeamBinding
import com.ssafy.tmbg.ui.main.AdminMainFragment

class TeamCreateDialog : DialogFragment() {
    private var _binding: CreateTeamBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            // TODO: 팀 생성 로직 구현
            dismiss() // 다이얼로그 닫기
            (parentFragment as? AdminMainFragment)?.navigateToTeam()
        }
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그 크기 설정
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = requireContext().resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.9).toInt()
            val height = (displayMetrics.heightPixels * 0.8).toInt()

            setLayout(width, height)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}