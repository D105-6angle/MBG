package com.ssafy.mbg.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.data.task.dao.Schedule
import com.ssafy.mbg.databinding.ItemScheduleBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    private val scheduleItems = mutableListOf<Schedule>()
    private val TAG = "ScheduleAdapter"

    class ScheduleViewHolder(private val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Schedule) {
            binding.apply {
                scheduleTitle.text = schedule.content.takeIf { it.isNotBlank() } ?: "제목 없음"

                try {
                    val inputFormat = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
                    val outputFormat = SimpleDateFormat(OUTPUT_TIME_FORMAT, Locale.getDefault())

                    val startDate = inputFormat.parse(schedule.startTime)
                    val endDate = inputFormat.parse(schedule.endTime)

                    val formattedStartTime = startDate?.let { outputFormat.format(it) }
                        ?: schedule.startTime
                    val formattedEndTime = endDate?.let { outputFormat.format(it) }
                        ?: schedule.endTime

                    scheduleTime.text = "$formattedStartTime - $formattedEndTime"
                } catch (e: Exception) {
                    Log.e("ScheduleAdapter", "Date parsing error: ${e.message}")
                    scheduleTime.text = "${schedule.startTime} - ${schedule.endTime}"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        if (position in scheduleItems.indices) {
            holder.bind(scheduleItems[position])
        } else {
            Log.e(TAG, "Invalid position: $position, size: ${scheduleItems.size}")
        }
    }

    override fun getItemCount(): Int = scheduleItems.size

    fun updateSchedules(schedules: List<Schedule>) {
        scheduleItems.clear()
        scheduleItems.addAll(schedules)
        notifyDataSetChanged()
    }

    companion object {
        private const val INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
        private const val OUTPUT_TIME_FORMAT = "HH:mm"
    }
}