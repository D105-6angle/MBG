package com.ssafy.mbg.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentNotificationBinding
import com.ssafy.mbg.adapter.NotificationAdapter
import com.ssafy.mbg.data.Notification

/**
 * 알림 목록을 표시하는 Fragment
 */
class NotificationFragment : DialogFragment() {

    // ViewBinding 객체
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    
    // RecyclerView 어댑터
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.NotificationDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadNotifications()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // X 버튼 클릭 시 닫기
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        // 배경 클릭 시 닫기
        dialog?.window?.decorView?.setOnClickListener {
            dismiss()
        }
        // 내부 클릭 이벤트 소비
        binding.root.setOnClickListener { }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setWindowAnimations(R.style.NotificationDialogAnimation)
                
                // 다이얼로그 크기 및 위치 설정
                setGravity(Gravity.START)
                setLayout(
                    resources.getDimensionPixelSize(R.dimen.notification_dialog_width),
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    }

    /**
     * RecyclerView 초기 설정
     * - 어댑터 설정
     * - 레이아웃 매니저 설정
     * - 아이템 구분선 추가
     */
    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter()
        binding.NotificationrecyclerView.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(context)
            // 아이템 사이에 구분선 추가
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    /**
     * 알림 데이터를 로드하고 표시
     * TODO: 실제 데이터를 가져오는 로직으로 변경 필요
     */
    private fun loadNotifications() {
        // 테스트용 더미 데이터
        val testNotifications = listOf(
            Notification(title = "제목1", body = "내용1", createAt = "2024-02-03"),
            Notification(title = "제목2", body = "내용2", createAt = "2024-02-03")
        )
        notificationAdapter.setNotifications(testNotifications)
    }

    /**
     * Fragment 파괴 시 ViewBinding 해제
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
