package com.ssafy.mbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.data.Schedule
import com.ssafy.mbg.databinding.ItemScheduleBinding

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    // 일정 데이터를 저장할 리스트
    private var schedules = listOf<Schedule>()

    // ViewHolder 클래스 정의: 개별 아이템 뷰를 관리
    class ScheduleViewHolder(private val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        // 아이템 뷰에 데이터를 설정하는 함수
        fun bind(schedule: Schedule) {
            binding.apply {
                numberCircle.text = schedule.number.toString() // 일정 번호 설정
                scheduleTitle.text = schedule.title // 일정 제목 설정
                scheduleTime.text = "${schedule.startTime} - ${schedule.endTime}" // 일정 시간 설정
            }
        }
    }

    // ViewHolder 생성 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    // ViewHolder와 데이터를 연결하는 함수
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(schedules[position])
    }

    // 아이템 개수를 반환하는 함수
    override fun getItemCount() = schedules.size

    // 새로운 일정 목록으로 업데이트하는 함수
    fun updateSchedules(newSchedules: List<Schedule>) {
        schedules = newSchedules
        notifyDataSetChanged() // 변경 사항을 RecyclerView에 알림
    }
}
