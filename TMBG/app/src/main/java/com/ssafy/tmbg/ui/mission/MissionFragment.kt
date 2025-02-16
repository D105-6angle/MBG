package com.ssafy.tmbg.ui.mission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.tmbg.databinding.FragmentMissionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MissionFragment : Fragment(){
    private lateinit var binding: FragmentMissionBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//          // 메모리 누수 방지
//    }
}