package com.ssafy.mbg.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.mbg.R
import com.ssafy.mbg.adapter.ProblemHistoryAdapter
import com.ssafy.mbg.data.ProblemHistory
import com.ssafy.mbg.databinding.FragmentPageBinding

class PageFragment : Fragment() {
    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private val problemHistoryAdapter = ProblemHistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBackButton()
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.problemHistory.apply {
            adapter = problemHistoryAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            // 뒤로가기 처리
            activity?.onBackPressed()
        }
    }

    private fun loadData() {
        // 더미 데이터 생성
        val histories = listOf(
            ProblemHistory(
                iconResId = R.drawable.cultural_1,
                title = "철퇴",
                lastSolvedAt = "문제 풀이 날짜 : 2024-01-23",
                result = true
            ),
            ProblemHistory(
                iconResId = R.drawable.sajungjeon,
                title = "사정전",
                lastSolvedAt = "문제 풀이 날짜 : 2024-01-20",
                result = true
            ),
            ProblemHistory(
                iconResId = R.drawable.cultural_2,
                title = "뒤주",
                lastSolvedAt = "문제 풀이 날짜 : 2024-01-19",
                result = false
            )
        )
        problemHistoryAdapter.updateHistories(histories)
    }
}