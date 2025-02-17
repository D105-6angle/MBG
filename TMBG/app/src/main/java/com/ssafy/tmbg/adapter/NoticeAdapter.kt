package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.notice.response.NoticeResponse
import com.ssafy.tmbg.databinding.ItemNoticeBinding

class NoticeAdapter : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {
    private var notices = listOf<NoticeResponse>()

    fun setNotices(newNotices: List<NoticeResponse>) {
        notices = newNotices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(notices[position])
    }

    override fun getItemCount() = notices.size

    class NoticeViewHolder(
        private val binding : ItemNoticeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notice : NoticeResponse) {
            binding.apply {
                tvContent.text = notice.content
            }
        }
    }
}