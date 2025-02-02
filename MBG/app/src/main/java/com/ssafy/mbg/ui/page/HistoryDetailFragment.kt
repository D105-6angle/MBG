package com.ssafy.mbg.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.mbg.databinding.FragmentHistoryDetailBinding

class HistoryDetailFragment : Fragment() {
    private var _binding: FragmentHistoryDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()
        loadHistoryDetail()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadHistoryDetail() {
        val args: HistoryDetailFragmentArgs by navArgs()

        binding.apply {
            titleText.text = args.title
            dateText.text = args.lastSolvedAt
            culturalImage.setImageResource(args.image)
            descriptionText.text = args.description
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}