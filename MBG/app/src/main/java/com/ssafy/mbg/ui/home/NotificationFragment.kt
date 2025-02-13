package com.ssafy.mbg.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ssafy.mbg.R
import com.ssafy.mbg.adapter.NotificationAdapter
import com.ssafy.mbg.data.Notification
import com.ssafy.mbg.databinding.FragmentNotificationBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : DialogFragment() {

    // ViewBinding 객체
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    // RecyclerView 어댑터
    private lateinit var notificationAdapter: NotificationAdapter

    @Inject
    lateinit var client: okhttp3.OkHttpClient

    @Inject
    lateinit var userPreferences: com.ssafy.mbg.di.UserPreferences

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
     * 알람 데이터를 GET 방식으로 조회하여 RecyclerView에 업데이트
     */
    private fun loadNotifications() {
        val userId = userPreferences.userId
        if (userId == null) {
            Toast.makeText(requireContext(), "User ID가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val url = HttpUrl.Builder()
            .scheme("https")
            .host("i12d106.p.ssafy.io")
            .addPathSegment("api")
            .addPathSegment("alarm")
            .addPathSegment(userId.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "알람 조회에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "오류: ${response.code}", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val gson = Gson()
                            val notifications = gson.fromJson(responseBody, Array<Notification>::class.java).toList()
                            requireActivity().runOnUiThread {
                                notificationAdapter.setNotifications(notifications)
                            }
                        } catch (e: Exception) {
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "데이터 파싱 오류", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
    }

    /**
     * Fragment 파괴 시 ViewBinding 해제
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
