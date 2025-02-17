package com.ssafy.tmbg.ui.notice

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.tmbg.adapter.NoticeAdapter
import com.ssafy.tmbg.data.notice.request.PushNoticeRequest
import com.ssafy.tmbg.databinding.FragmentNoticeBinding
import com.ssafy.tmbg.ui.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoticeFragment : Fragment() {
    private lateinit var binding: FragmentNoticeBinding
    private val noticeViewModel: NoticeViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val noticeAdapter = NoticeAdapter()
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupRecyclerView()
        observeNoticeState()
        observeRoomId()
    }

    private fun setupRecyclerView() {
        binding.rvNotices.apply {
            adapter = noticeAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            btnEditCurrentNotice.setOnClickListener {
                isEditing = !isEditing
                updateEditingUI()
            }
        }
    }

    private fun updateEditingUI() {
        binding.apply {
            etCurrentNotice.isEnabled = isEditing

            if (isEditing) {
                etCurrentNotice.text = null
                btnEditCurrentNotice.text = "저장"
                etCurrentNotice.requestFocus()
                showKeyboard(etCurrentNotice)
            } else {
                btnEditCurrentNotice.text = "수정"
                etCurrentNotice.clearFocus()
                hideKeyboard(etCurrentNotice)

                val content = etCurrentNotice.text.toString()
                if (content.isNotBlank()) {
                    val currentRoomId = sharedViewModel.roomId.value
                    if (currentRoomId != -1) {
                        noticeViewModel.createNotice(currentRoomId.toLong(), content)
                    }
                }
            }
        }
    }

    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun observeNoticeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            noticeViewModel.state.collect { state ->
                when (state) {
                    is NoticeState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is NoticeState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        noticeAdapter.setNotices(state.notices)
                        // 최신 공지를 현재 공지란에 표시
                        state.notices.firstOrNull()?.let { latestNotice ->
                            binding.etCurrentNotice.setText(latestNotice.content)
                        }
                    }
                    is NoticeState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    NoticeState.Initial -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun observeRoomId() {
        viewLifecycleOwner.lifecycleScope.launch {
            // 초기 로딩
            val currentRoomId = sharedViewModel.roomId.value
            if (currentRoomId != -1) {
                noticeViewModel.fetchNotices(currentRoomId.toLong())
            }

            // 이후 변경사항 관찰
            sharedViewModel.roomId.collect { roomId ->
                if (roomId != -1) {
                    noticeViewModel.fetchNotices(roomId.toLong())
                }
            }
        }
    }
}