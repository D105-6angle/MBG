package com.ssafy.mbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.data.Notification
import com.ssafy.mbg.databinding.ItemNotificationBinding

/**
 * 알림 목록을 표시하기 위한 RecyclerView 어댑터
 */
class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    /** 표시할 알림 목록 */
    private val notifications = mutableListOf<Notification>()

    /**
     * 알림 목록을 업데이트합니다.
     *
     * @param newNotifications 새로운 알림 목록
     */
    fun setNotifications(newNotifications: List<Notification>) {
        notifications.clear()
        notifications.addAll(newNotifications)
        notifyDataSetChanged()
    }

    /**
     * ViewHolder를 생성합니다.
     *
     * @param parent 부모 ViewGroup
     * @param viewType 뷰 타입 (현재는 단일 타입만 사용)
     * @return 생성된 ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    /**
     * ViewHolder에 데이터를 바인딩합니다.
     *
     * @param holder 데이터를 표시할 ViewHolder
     * @param position 데이터 위치
     */
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size

    /**
     * 알림 아이템을 표시하기 위한 ViewHolder
     *
     * @property binding 아이템 레이아웃 바인딩
     */
    class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * 알림 데이터를 뷰에 바인딩합니다.
         *
         * @param notification 표시할 알림 데이터
         */
        fun bind(notification: Notification) {
            binding.apply {
                notificationTitle.text = notification.title
                notificationBody.text = notification.body
                notificationDate.text = notification.createAt
            }
        }
    }
}
