package com.ssafy.mbg.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentHistoryDetailBinding
import com.ssafy.mbg.di.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HistoryDetailFragment : Fragment() {
    private var _binding: FragmentHistoryDetailBinding? = null
    private val binding get() = _binding!!

    private val myPageViewModel: MyPageViewModel by viewModels()

    @Inject
    lateinit var userPreferences: UserPreferences

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

        binding.apply {
            progressBar.visibility = View.VISIBLE
            contentLayout.visibility = View.GONE

        }

        setupBackButton()
        observeMyPageState()
        loadDetailData()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeMyPageState() {
        viewLifecycleOwner.lifecycleScope.launch {
            myPageViewModel.uiState.collect { state ->
                when (state) {
                    is MyPageState.Loading -> {
                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                            contentLayout.visibility = View.GONE
                        }
                    }

                    is MyPageState.Success -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            contentLayout.visibility = View.VISIBLE

                            state.problemResponse?.let { problem ->
                                titleText.text = problem.cardName
                                dateText.text = problem.lastAttemptedAt

                                Glide.with(requireContext())
                                    .load(problem.imageUrl)
                                    .placeholder(R.drawable.placeholder_image)
                                    .error(R.drawable.error_image)
                                    .into(culturalImage)

                                descriptionText.text = problem.description
                            }
                        }
                    }

                    is MyPageState.Error -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            contentLayout.visibility = View.VISIBLE
                        }
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun loadDetailData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val args: HistoryDetailFragmentArgs by navArgs()
            userPreferences.userId?.let { userId ->
                myPageViewModel.getDetailProblem(userId, args.cardId)
            }
        }
    }
}