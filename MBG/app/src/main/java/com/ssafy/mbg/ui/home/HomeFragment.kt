package com.ssafy.mbg.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.FragmentHomeBinding
import com.ssafy.mbg.di.UserPreferences
import com.ssafy.mbg.ui.chatbot.ChatBotDialogFragment
import com.ssafy.mbg.ui.home.InviteCodeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferences
    
    private val viewModel: HomeViewModel by viewModels()

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // fragment_home.xml 레이아웃 파일이 있어야 합니다.
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()

        // isJoinedGroup 상태 관찰
        viewModel.isJoinedGroup.observe(viewLifecycleOwner) { isJoined ->
            if (isJoined) {
                showJoinedGroupUI()
            } else {
                showNotJoinedGroupUI()
            }
        }

        // userPreferences의 location 변경 감지
        lifecycleScope.launch {
            userPreferences.locationFlow.collect { location ->
                Log.d("HomeFragment", "Location flow updated: $location")
                updateLocationUI(location)
            }
        }

////////////////////////////////////////////////////////////////////////////
         // 테스트용 clearGroup 버튼 추가
//         binding.root.post {
//             val clearButton = Button(requireContext()).apply {
//                 text = "Clear Group (Test)"
//                 setOnClickListener {
//                     viewModel.clearGroup()
//                 }
//                 layoutParams = ViewGroup.LayoutParams(
//                     ViewGroup.LayoutParams.WRAP_CONTENT,
//                     ViewGroup.LayoutParams.WRAP_CONTENT
//                 )
//             }
//
//             // 버튼을 기존 레이아웃의 하단에 추가
//             if (binding.root is ViewGroup) {
//                 (binding.root as ViewGroup).addView(clearButton)
//             }
//         }

///////////////////////////////////////////////////////////////////////////////
    }

    private fun setupClickListeners() {
        with(binding) {
            notificationIcon.setOnClickListener {
                NotificationFragment().show(parentFragmentManager, "NotificationFragment")
            }
            
            teamIcon.setOnClickListener {
                Log.d("HomeFragment", "teamIcon clicked")
                InviteCodeFragment().show(parentFragmentManager, "InviteCodeFragment")
            }
            
            questionIcon.setOnClickListener {
                ChatBotDialogFragment().show(childFragmentManager, "chatbot")
            }
        }
    }

    private fun showJoinedGroupUI() {
        with(binding) {
            // 팀 아이콘 숨기기
            teamIcon.visibility = View.GONE
            
            // 위치 정보 표시
            locationText.apply {
                text = if (userPreferences.location.isNotEmpty()) {
                    userPreferences.location
                } else {
                    "지역을 선택해 주세요"
                }
                visibility = View.VISIBLE
                setOnClickListener {
                    showLocationSelectionDialog()
                }
            }
            
            // 팀 번호와 역할 표시
            teamNumberText.apply {
                text = "${userPreferences.groupNo}조 ${if (userPreferences.codeId == "J001") "조장" else "조원"}"
                visibility = View.VISIBLE
            }

            // 캐릭터 GIF 표시
            Glide.with(requireContext())
                .asGif()
                .load(R.drawable.character_gif_origin)
                .into(characterGif)
            characterGif.visibility = View.VISIBLE

            // 알림 아이콘과 질문 아이콘 표시
            notificationIcon.visibility = View.VISIBLE
            questionIcon.visibility = View.VISIBLE
        }
    }

    private fun showNotJoinedGroupUI() {
        with(binding) {
            // 팀 선택 아이콘 표시
            teamIcon.visibility = View.VISIBLE
            
            // 위치 정보 표시 (숨기지 않고 표시)
            locationText.apply {
                text = if (userPreferences.location.isNotEmpty()) {
                    userPreferences.location
                } else {
                    "지역을 선택해 주세요"
                }
                visibility = View.VISIBLE  // GONE에서 VISIBLE로 변경
                setOnClickListener {
                    showLocationSelectionDialog()
                }
            }
            
            // 팀 번호 텍스트 숨기기
            teamNumberText.visibility = View.GONE

            // 캐릭터 GIF 표시
            Glide.with(requireContext())
                .asGif()
                .load(R.drawable.character_gif_origin)
                .into(characterGif)
            characterGif.visibility = View.VISIBLE

            // 알림 아이콘과 질문 아이콘 표시
            notificationIcon.visibility = View.VISIBLE
            questionIcon.visibility = View.VISIBLE
        }
    }

    private fun updateLocationUI(location: String) {
        Log.d("HomeFragment", "Updating UI with location: $location")
        binding.locationText.apply {
            text = if (location.isNotEmpty()) {
                location
            } else {
                "지역을 선택해 주세요"
            }
        }
    }

    private fun showLocationSelectionDialog() {
        val locations = arrayOf("경복궁", "인동향교")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("지역 선택")
            .setItems(locations) { dialog, which ->
                val selectedLocation = locations[which]
                Log.d("HomeFragment", "Location selected from dialog: $selectedLocation")
                // UserPreferences 업데이트
                viewModel.updateLocation(selectedLocation)
            }
            .show()
    }

    fun navigateToRoomList(numOfGroups: Long, roomId: Long, location: String) {
        Log.d("HomeFragment", "navigateToRoomList called with numOfGroups: $numOfGroups, roomId: $roomId, location: $location")
        findNavController().navigate(
            R.id.action_homeFragment_to_roomListFragment,
            bundleOf(
                "numOfGroups" to numOfGroups,
                "roomId" to roomId,
                "location" to location
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
