package com.ssafy.mbg.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ssafy.mbg.R
import com.ssafy.mbg.adapter.RoomAdapter
import com.ssafy.mbg.databinding.FragmentRoomListBinding
import com.ssafy.mbg.di.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RoomListFragment : Fragment() {
    private var _binding: FragmentRoomListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private var currentRoomId: Long = 0L

    @Inject
    lateinit var userPreferences: UserPreferences

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

        arguments?.let { args ->
            val numOfGroups = args.getLong("numOfGroups")
            currentRoomId = args.getLong("roomId")
            setupRecyclerView(numOfGroups)
            setupObservers()
        }
        
        // 닫기 버튼 클릭 리스너 추가
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        viewModel.joinGroupResult.observe(viewLifecycleOwner) { response ->
            // 그룹 선택 시 UserPreferences에 저장
            userPreferences.groupNo = response.groupNo
            userPreferences.codeId = response.codeId

            findNavController().navigate(
                R.id.action_roomListFragment_to_homeFragment,
                bundleOf(
                    "selected_team" to response.groupNo.toLong(),
                    "code_id" to response.codeId
                )
            )
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView(numOfGroups: Long) {
        val items = (1..numOfGroups).map { it.toInt() }
        
        val adapter = RoomAdapter(items) { selectedGroup ->
            viewModel.joinGroup(currentRoomId, selectedGroup)
        }

        binding.rvRooms.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 