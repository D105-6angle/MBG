package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.report.Attendance
import com.ssafy.tmbg.databinding.ItemAttendanceBinding

class AttendanceAdapter : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    private var attendanceList = emptyList<Attendance>()

    inner class ViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Attendance, position: Int) {
            binding.apply {
                // 번호는 01, 02와 같은 형식으로 표시
                studentNumber.text = String.format("%02d", position + 1)
                studentName.text = data.name

                // 아이템 애니메이션
                root.alpha = 0f
                root.translationY = 20f

                root.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay(position * 50L)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
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
        holder.bind(attendanceList[position], position)
    }

    override fun getItemCount() = attendanceList.size

    fun getCurrentList(): List<Attendance> = attendanceList

    fun updateAttendance(newList: List<Attendance>) {
        attendanceList = newList
        notifyDataSetChanged()
    }
}