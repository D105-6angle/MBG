package com.ssafy.mbg.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentHomeBinding
import com.ssafy.mbg.ui.chatbot.ChatBotDialogFragment

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // fragment_home.xml 레이아웃 파일이 있어야 합니다.
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .asGif()
            .load(R.drawable.character_gif_origin)
            .into(binding.characterGif)
        
        // 전달받은 팀 번호가 있다면 처리
        arguments?.getInt("selected_team")?.let { teamNumber ->
            binding.teamIcon.visibility = View.GONE
            binding.teamNumberText.apply {
                text = "${teamNumber}조"
                visibility = View.VISIBLE
            }
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.notificationIcon.setOnClickListener {
            NotificationFragment().show(parentFragmentManager, "NotificationFragment")
        }
        binding.teamIcon.setOnClickListener {
            InviteCodeFragment().show(parentFragmentManager, "InviteCodeFragment")
        }
        binding.questionIcon.setOnClickListener {
            ChatBotDialogFragment().show(childFragmentManager, "chatbot")
        }


//        binding.questionIcon.setOnClickListener {
//            ChatBotFragment().view

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
