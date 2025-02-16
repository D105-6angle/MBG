package com.ssafy.mbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.mbg.R
import com.ssafy.mbg.data.mypage.dao.AttemptedProblem

import com.ssafy.mbg.databinding.ItemProblemHistoryBinding

class ProblemHistoryAdapter(
    private val onItemClick: (AttemptedProblem) -> Unit
) : RecyclerView.Adapter<ProblemHistoryAdapter.ProblemHistoryHolder>() {

    private var problemHistories = listOf<AttemptedProblem>()

    inner class ProblemHistoryHolder(private val binding: ItemProblemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: AttemptedProblem) {
            binding.apply {
                // 이미지 로드
                Glide.with(itemView.context)
                    .load(history.imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .into(itemIcon)

                // 제목 설정
                itemTitle.text = history.name

                // 날짜 설정
                itemDate.text = history.lastAttempedAt

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

    fun updateHistories(newHistories: List<AttemptedProblem>) {
        problemHistories = newHistories
        notifyDataSetChanged()
    }
}