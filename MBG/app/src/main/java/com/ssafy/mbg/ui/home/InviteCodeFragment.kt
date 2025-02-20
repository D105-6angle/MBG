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
import com.ssafy.mbg.databinding.FragmentInviteCodeBinding
import com.ssafy.mbg.di.UserPreferences
import com.ssafy.mbg.data.home.dao.JoinRoomRequest
import com.ssafy.mbg.data.home.dao.JoinGroupResponse
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 초대 코드를 입력받아 방에 참여하는 다이얼로그 프래그먼트
 * 
 * 주요 기능:
 * 1. 초대 코드 입력 UI 제공
 * 2. 입력된 코드로 방 참여 요청
 * 3. 참여 성공시 RoomListFragment로 네비게이션
 */
@AndroidEntryPoint
class InviteCodeFragment : DialogFragment() {

    // ViewBinding 객체. onDestroyView에서 메모리 누수 방지를 위해 null 처리 필요
    private var _binding: FragmentInviteCodeBinding? = null
    private val binding get() = _binding!!

    // Hilt를 통한 ViewModel 주입
    private val viewModel: HomeViewModel by viewModels()
    
    // 사용자 설정 데이터 저장소
    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("InviteCodeFragment", "onCreate called")
        // 다이얼로그의 타이틀바를 제거하고 투명 스타일 적용
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)
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

    /**
     * 모든 클릭 리스너와 LiveData 옵저버를 설정
     * 
     * 1. 닫기 버튼: 다이얼로그 종료
     * 2. 제출 버튼: 입력된 초대 코드로 방 참여 요청
     * 3. LiveData 옵저버들: 서버 응답 처리
     */
    private fun setupClickListeners() {
        // 닫기 버튼 클릭시 다이얼로그 종료
        binding.closeButton.setOnClickListener {
            dismiss()
        }

        // 제출 버튼 클릭시 초대 코드로 방 참여 요청
        binding.submitButton.setOnClickListener {
            val inviteCode = binding.inviteCodeInput.text.toString()
            if (inviteCode.isNotEmpty()) {
                viewModel.joinRoom(JoinRoomRequest(inviteCode))
            }
        }

        // 위치 정보 응답 처리
        viewModel.location.observe(viewLifecycleOwner) { location ->
            location?.let {
                if (it.isNotEmpty()) {
                    userPreferences.location = it  // 위치 정보 저장
                }
            }
        }

        // 방 ID 응답 처리
        viewModel.roomId.observe(viewLifecycleOwner) { roomId ->
            if (roomId > 0L) {
                userPreferences.roomId = roomId  // 방 ID 저장만 하고 그룹 정보는 설정하지 않음
                
                // location과 numOfGroups 값이 모두 있을 때 조 선택 화면으로 이동
                viewModel.location.value?.let { location ->
                    viewModel.numOfGroups.value?.let { numOfGroups ->
                        if (location.isNotEmpty() && numOfGroups > 0) {
                            navigateToRoomList(
                                numOfGroups = numOfGroups,
                                roomId = roomId,
                                location = location
                            )
                        }
                    }
                }
            }
        }

        // 에러 응답 처리
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }

        // 그룹 수 응답 처리
        viewModel.numOfGroups.observe(viewLifecycleOwner) { numOfGroups ->
            Log.d("InviteCodeFragment", "numOfGroups observer triggered: $numOfGroups")
            if (numOfGroups > 0L) {
                Log.d("InviteCodeFragment", "Attempting to navigate with numOfGroups: $numOfGroups")
                
                // roomId와 location 값이 모두 있을 때 조 선택 화면으로 이동
                viewModel.roomId.value?.let { roomId ->
                    viewModel.location.value?.let { location ->
                        if (location.isNotEmpty() && roomId != 0L) {
                            navigateToRoomList(
                                numOfGroups = numOfGroups,
                                roomId = roomId,
                                location = location
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * 그룹 참여 성공 처리
     * 
     * @param joinGroupResponse 그룹 참여 응답 데이터
     */
    private fun handleJoinGroupSuccess(joinGroupResponse: JoinGroupResponse) {
        // 그룹 번호와 코드 ID를 UserPreferences에 저장
        userPreferences.apply {
            groupNo = joinGroupResponse.groupNo
            codeId = joinGroupResponse.codeId
        }
        
        // 다이얼로그 종료
        dismiss()
    }

    /**
     * 조 선택 화면으로 네비게이션
     * 
     * @param numOfGroups 전체 그룹 수
     * @param roomId 방 ID
     * @param location 위치 정보
     */
    private fun navigateToRoomList(numOfGroups: Long, roomId: Long, location: String) {
        // RoomListFragment로 네비게이션하며 필요한 데이터 전달
        findNavController().navigate(
            R.id.action_homeFragment_to_roomListFragment,
            bundleOf(
                "numOfGroups" to numOfGroups,
                "roomId" to roomId,
                "location" to location
            )
        )
        dismiss()  // 네비게이션 후 다이얼로그 종료
    }

    /**
     * 다이얼로그 설정
     * - 배경을 투명하게 설정
     * - 화면 중앙에 위치
     * - 크기 설정
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                setGravity(Gravity.CENTER)
                
                val params = attributes
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                attributes = params
            }
        }
    }

    /**
     * 다이얼로그 시작시 설정
     * - 너비를 화면의 90%로 설정
     * - 배경을 투명하게 설정
     */
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.heightPixels * 0.5).toInt()
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    /**
     * 뷰 파괴시 바인딩 객체 정리
     * 메모리 누수 방지
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
