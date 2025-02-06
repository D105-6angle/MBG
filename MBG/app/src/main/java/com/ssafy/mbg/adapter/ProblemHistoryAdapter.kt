package com.ssafy.mbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.data.mypage.dto.ProblemHistory
import com.ssafy.mbg.databinding.ItemProblemHistoryBinding

class ProblemHistoryAdapter(
    private val onItemClick : (ProblemHistory) -> Unit
) : RecyclerView.Adapter<ProblemHistoryAdapter.ProblemHistoryHolder>() {

    private var problemHistories = listOf<ProblemHistory>()

    inner class ProblemHistoryHolder(private val binding: ItemProblemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: ProblemHistory) {
            binding.apply {
                // 아이콘 설정
                itemIcon.setImageResource(history.imageUrl)

                // 제목 설정
                itemTitle.text = history.name

                // 날짜 포맷팅 및 설정
                itemDate.text = history.lastSolvedAt

                // 클릭 리스너 설정
                root.setOnClickListener {
                    onItemClick(history)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProblemHistoryHolder {
        val binding = ItemProblemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProblemHistoryHolder(binding)
    }

    override fun onBindViewHolder(holder: ProblemHistoryHolder, position: Int) {
        holder.bind(problemHistories[position])
    }

    override fun getItemCount() = problemHistories.size

    fun updateHistories(newHistories: List<ProblemHistory>) {
        problemHistories = newHistories
        notifyDataSetChanged()
    }
}