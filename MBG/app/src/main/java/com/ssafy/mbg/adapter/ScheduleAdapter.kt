package com.ssafy.mbg.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.data.task.dao.Schedule

import com.ssafy.mbg.data.task.dao.ScheduleResponse
import com.ssafy.mbg.databinding.ItemScheduleBinding
import com.ssafy.mbg.ui.task.ScheduleState
import java.text.SimpleDateFormat
import java.util.Locale

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    private var scheduleItems = listOf<Schedule>()
    private val TAG = "ScheduleAdapter"

    class ScheduleViewHolder(private val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Schedule) {
            try {
                binding.apply {
                    scheduleTitle.text = schedule.content.takeIf { it.isNotBlank() } ?: "제목 없음"

                    try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

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
            } catch (e: Exception) {
                Log.e("ScheduleAdapter", "Binding error: ${e.message}")
                binding.scheduleTitle.text = "오류 발생"
                binding.scheduleTime.text = "시간 정보 없음"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return try {
            val binding = ItemScheduleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ScheduleViewHolder(binding)
        } catch (e: Exception) {
            Log.e(TAG, "ViewHolder creation error: ${e.message}")
            val fallbackBinding = ItemScheduleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ScheduleViewHolder(fallbackBinding)
        }
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        try {
            if (position in scheduleItems.indices) {
                holder.bind(scheduleItems[position])
            } else {
                Log.e(TAG, "Invalid position: $position, size: ${scheduleItems.size}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Binding error at position $position: ${e.message}")
        }
    }

    override fun getItemCount(): Int {
        return try {
            scheduleItems.size
        } catch (e: Exception) {
            Log.e(TAG, "Error getting item count: ${e.message}")
            0
        }
    }

    fun updateSchedules(schedules: List<Schedule>) {
        try {
            scheduleItems = schedules
            notifyDataSetChanged()
        } catch (e: Exception) {
            Log.e(TAG, "Update error: ${e.message}")
            scheduleItems = emptyList()
            notifyDataSetChanged()
        }
    }

    companion object {
        private const val DEFAULT_TIME_FORMAT = "HH:mm"
        private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    }
}