package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.databinding.ItemScheduleBinding
import com.ssafy.tmbg.data.schedule.Schedule
import java.text.SimpleDateFormat
import java.util.*

class ScheduleAdapter : ListAdapter<Schedule, ScheduleAdapter.ViewHolder>(ScheduleDiffCallback()) {

    private var onEditClick: ((Schedule) -> Unit)? = null
    private var onDeleteClick: ((Schedule) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemScheduleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemScheduleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(schedule: Schedule) {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeString = "${timeFormat.format(schedule.startTime)} ~ ${timeFormat.format(schedule.endTime)}"
            
            binding.tvTime.text = timeString
            binding.tvTitle.text = schedule.content

            binding.btnEdit.setOnClickListener {
                onEditClick?.invoke(schedule)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick?.invoke(schedule)
            }
        }
    }

    fun setOnEditClickListener(listener: (Schedule) -> Unit) {
        onEditClick = listener
    }

    fun setOnDeleteClickListener(listener: (Schedule) -> Unit) {
        onDeleteClick = listener
    }
}

private class ScheduleDiffCallback : DiffUtil.ItemCallback<Schedule>() {
    override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem.schedulesId == newItem.schedulesId
    }

    override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem == newItem
    }
} 