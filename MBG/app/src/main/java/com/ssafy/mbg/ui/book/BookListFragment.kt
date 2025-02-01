package com.ssafy.mbg.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ssafy.mbg.adapter.BookAdapter
import com.ssafy.mbg.data.Card
import com.ssafy.mbg.databinding.FragmentBookListBinding

/**
 * 도감의 카드 목록을 표시하는 Fragment
 * 
 * @property isCultural 문화재 탭 여부 (true: 문화재 탭, false: 스토리 탭)
 */
class BookListFragment(private val isCultural: Boolean) : Fragment() {
    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadCards()
    }

    /**
     * RecyclerView 초기 설정
     */
    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(parentFragmentManager)
        binding.bookRecyclerView.apply {
            adapter = bookAdapter
            layoutManager = GridLayoutManager(context, 3)
        }
    }

    /**
     * 카드 목록을 새로고침합니다.
     */
    fun refreshCards() {
        if (!isAdded) return  // Fragment가 아직 추가되지 않은 경우 무시

        val testCards = (1..30).map { id ->
            Card(id, isUnlocked = isCultural && id <= 3)
        }
        bookAdapter.setCards(testCards)
    }

    /**
     * 카드 데이터 로드 및 표시
     */
    private fun loadCards() {
        refreshCards()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 