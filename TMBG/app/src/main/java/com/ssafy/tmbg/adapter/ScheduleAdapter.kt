package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.databinding.ItemScheduleBinding
import com.ssafy.tmbg.data.schedule.dao.Schedule
import java.text.SimpleDateFormat
import java.util.*

/**
 * 일정 목록을 RecyclerView에 표시하기 위한 어댑터
 * ListAdapter를 상속받아 일정 목록의 변경사항을 효율적으로 처리합니다.
 */
class ScheduleAdapter : ListAdapter<Schedule, ScheduleAdapter.ViewHolder>(ScheduleDiffCallback()) {

    // 수정/삭제 버튼 클릭 리스너
    private var onEditClick: ((Schedule) -> Unit)? = null
    private var onDeleteClick: ((Schedule) -> Unit)? = null

    /**
     * ViewHolder를 생성합니다.
     * 
     * 동작 과정:
     * 1. 아이템 레이아웃을 inflate
     * 2. ViewHolder 인스턴스 생성 및 반환
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemScheduleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * 지정된 위치의 데이터를 ViewHolder에 바인딩합니다.
     * 
     * @param holder 데이터를 표시할 ViewHolder
     * @param position 데이터 위치
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * 일정 데이터를 화면에 표시하는 ViewHolder
     * 
     * @property binding 아이템 레이아웃 바인딩 객체
     */
    inner class ViewHolder(
        private val binding: ItemScheduleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * 일정 데이터를 뷰에 바인딩합니다.
         * 
         * 동작 과정:
         * 1. 시간 포맷 설정 (HH:mm)
         * 2. 시작~종료 시간 문자열 생성
         * 3. 시간과 내용을 뷰에 설정
         * 4. 수정/삭제 버튼 클릭 리스너 설정
         */
        fun bind(schedule: Schedule) {
            // 시간 포맷 설정
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            // 시작~종료 시간 문자열 생성
            val timeString = "${timeFormat.format(schedule.startTime)} ~ ${timeFormat.format(schedule.endTime)}"
            
            // 시간과 내용을 뷰에 설정
            binding.tvTime.text = timeString
            binding.tvTitle.text = schedule.content

            // 수정/삭제 버튼 클릭 리스너 설정
            binding.btnEdit.setOnClickListener {
                onEditClick?.invoke(schedule)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick?.invoke(schedule)
            }
        }
    }

    /**
     * 수정 버튼 클릭 리스너를 설정합니다.
     * @param listener 클릭된 일정을 파라미터로 받는 콜백 함수
     */
    fun setOnEditClickListener(listener: (Schedule) -> Unit) {
        onEditClick = listener
    }

    /**
     * 삭제 버튼 클릭 리스너를 설정합니다.
     * @param listener 클릭된 일정을 파라미터로 받는 콜백 함수
     */
    fun setOnDeleteClickListener(listener: (Schedule) -> Unit) {
        onDeleteClick = listener
    }
}

/**
 * 일정 목록의 변경사항을 감지하고 처리하는 DiffUtil 콜백
 */
private class ScheduleDiffCallback : DiffUtil.ItemCallback<Schedule>() {
    /**
     * 두 아이템이 같은 일정인지 확인합니다.
     * schedulesId를 기준으로 판단합니다.
     */
    override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem.scheduleId == newItem.scheduleId
    }

    /**
     * 두 아이템의 내용이 같은지 확인합니다.
     * Schedule 데이터 클래스의 equals 메서드 사용
     */
    override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem == newItem
    }
} 