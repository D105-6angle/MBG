package com.ssafy.mbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.data.ProblemHistory
import com.ssafy.mbg.databinding.ItemProblemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ProblemHistoryAdapter : RecyclerView.Adapter<ProblemHistoryAdapter.ProblemHistoryHolder>() {

    private var problemHistories = listOf<ProblemHistory>()

    class ProblemHistoryHolder(private val binding: ItemProblemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

        fun bind(history: ProblemHistory) {
            binding.apply {
                // 아이콘 설정
                itemIcon.setImageResource(history.iconResId)

                // 제목 설정
                itemTitle.text = history.title

                // 날짜 포맷팅 및 설정
                itemDate.text = history.lastSolvedAt.substring(0, 10)
                    .replace("-", ".")
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