package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.report.Attendance
import com.ssafy.tmbg.databinding.ItemAttendanceBinding

class AttendanceAdapter : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    private var attendanceList = emptyList<Attendance>()

    inner class ViewHolder(private val binding : ItemAttendanceBinding):
            RecyclerView.ViewHolder(binding.root) {

                fun bind(data: Attendance) {
                    binding.studentName.text = data.name
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAttendanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(attendanceList[position])
    }

    override fun getItemCount() = attendanceList.size

    fun getCurrentList(): List<Attendance> = attendanceList

    fun updateAttendance(newList: List<Attendance>) {
        attendanceList = newList
        notifyDataSetChanged()
    }
}