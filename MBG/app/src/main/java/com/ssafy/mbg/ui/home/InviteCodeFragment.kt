package com.ssafy.mbg.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.mbg.R
import com.ssafy.mbg.adapter.RoomAdapter
import com.ssafy.mbg.databinding.FragmentInviteCodeBinding
import com.ssafy.mbg.di.UserPreferences
import com.ssafy.mbg.data.home.dao.JoinRequest
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InviteCodeFragment : DialogFragment() {

    private var _binding: FragmentInviteCodeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    
    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("InviteCodeFragment", "onCreate called")
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)  // TransparentDialog 스타일 사용
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("InviteCodeFragment", "onCreateView called")
        _binding = FragmentInviteCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.submitButton.setOnClickListener {
            val inviteCode = binding.inviteCodeInput.text.toString()
            if (inviteCode.isNotEmpty()) {
                viewModel.joinRoom(JoinRequest(inviteCode))
            }
        }

        // 응답 관찰
        viewModel.location.observe(viewLifecycleOwner) { location ->
            location?.let {
                if (it.isNotEmpty()) {  // 빈 문자열이 아닐 때만 처리
                    userPreferences.location = it
                }
            }
        }

        viewModel.roomId.observe(viewLifecycleOwner) { roomId ->
            if (roomId != 0L) {
                userPreferences.roomId = roomId
                dismiss()
                // HomeFragment로 돌아가서 팀 번호 표시
                (parentFragment as? HomeFragment)?.updateTeamDisplay(roomId)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.numOfGroups.observe(viewLifecycleOwner) { numOfGroups ->
            if (numOfGroups > 0L) {  // 0보다 클 때만 처리
                dismiss()
                (parentFragment as? HomeFragment)?.navigateToRoomList(
                    numOfGroups = numOfGroups,
                    roomId = viewModel.roomId.value ?: 0L,
                    location = viewModel.location.value ?: ""
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                
                // 다이얼로그를 화면 중앙에 위치시킴
                setGravity(Gravity.CENTER)
                
                // 다이얼로그 크기 설정
                val params = attributes
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                attributes = params
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),  // 화면 너비의 90%
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 