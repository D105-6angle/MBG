package com.ssafy.mbg.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ssafy.mbg.R
import com.ssafy.mbg.adapter.RoomAdapter
import com.ssafy.mbg.databinding.FragmentRoomListBinding

class RoomListFragment : Fragment() {
    private var _binding: FragmentRoomListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
    }

    // 임시적으로 부여할 방 번호 리스트
    private fun setupRecyclerView() {
        val rooms = listOf(1, 2, 3, 4) // 방 번호 리스트
        val adapter = RoomAdapter(rooms) { roomNumber ->
        //방 클릭시 처리 로직
            onRoomClick(roomNumber)
        }

        binding.rvRooms.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(context, 2) // 2열 그리드 레이아웃
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            // HomeFragment로 돌아가기
            parentFragmentManager.popBackStack()
        }
    }

    private fun onRoomClick(roomNumber: Int) {
        // Bundle을 통해 선택된 팀 번호 전달
        findNavController().navigate(
            R.id.action_roomListFragment_to_homeFragment,
            bundleOf("selected_team" to roomNumber)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 