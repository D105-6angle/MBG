package com.ssafy.mbg.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.ssafy.mbg.R
import com.ssafy.mbg.adapter.BookPagerAdapter
import com.ssafy.mbg.data.book.response.BookResponse
import com.ssafy.mbg.databinding.FragmentBookBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BookFragment : Fragment() {
    private var _binding: FragmentBookBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabButtons()
        observeState()
        fetchInitialData()
    }

    private fun setupViewPager() {
        binding.viewPager.apply {
            adapter = BookPagerAdapter(this@BookFragment).apply {
                // 초기 데이터 설정 - 빈 BookResponse 객체 생성
                updateData(BookResponse(
                    totalCards = 0,
                    totalHeritageCards = 0,
                    totalStoryCards = 0,
                    cards = emptyList()
                ))
            }
            offscreenPageLimit = 2

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateTabSelection(position == 0)
                }
            })
        }
    }

    private fun fetchInitialData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUserId()?.let { userId ->
                viewModel.getBook(userId)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bookState.collect { state ->
                when (state) {
                    is BookState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.viewPager.visibility = View.GONE
                    }
                    is BookState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.viewPager.visibility = View.VISIBLE
                        // BookResponse 전체를 전달
                        (binding.viewPager.adapter as? BookPagerAdapter)?.updateData(state.bookResponse)
                        val bookAdapter = binding.viewPager.adapter as BookPagerAdapter
                        val heritageCount = bookAdapter.getHeritageCardsCount()
                        val storyCount = bookAdapter.getStoryCardsCount()
                    }
                    is BookState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.viewPager.visibility = View.VISIBLE
                        Snackbar.make(
                            binding.root,
                            state.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    BookState.Initial -> {
                        binding.viewPager.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupTabButtons() {
        binding.culturalTab.setOnClickListener {
            updateTabSelection(true)
            binding.viewPager.setCurrentItem(0, true)
        }

        binding.storyTab.setOnClickListener {
            updateTabSelection(false)
            binding.viewPager.setCurrentItem(1, true)
        }
    }

    private fun updateTabSelection(isCulturalTab: Boolean) {
        binding.culturalTab.setBackgroundResource(
            if (isCulturalTab) R.drawable.button_selected else R.drawable.button_unselected
        )
        binding.storyTab.setBackgroundResource(
            if (isCulturalTab) R.drawable.button_unselected else R.drawable.button_selected
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}