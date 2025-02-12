package com.ssafy.mbg.ui.ox_quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ssafy.mbg.databinding.FragmentOxquizBinding

class OXQuizFragment : Fragment(){
    private var _binding : FragmentOxquizBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOxquizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnStart.setOnClickListener {
            findNavController().navigate(
                OXQuizFragmentDirections.actionOxQuizToQuizQuestion()
            )
        }
    }
}