package com.ssafy.mbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.data.Schedule
import com.ssafy.mbg.databinding.ItemScheduleBinding

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    private var schedules = listOf<Schedule>()

    class ScheduleViewHolder(private val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Schedule) {
            binding.apply {
                numberCircle.text = schedule.number.toString()
                scheduleTitle.text = schedule.title
                scheduleTime.text = "${schedule.startTime} - ${schedule.endTime}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(schedules[position])
    }

    override fun getItemCount() = schedules.size

    fun updateSchedules(newSchedules: List<Schedule>) {
        schedules = newSchedules
        notifyDataSetChanged()
    }
}