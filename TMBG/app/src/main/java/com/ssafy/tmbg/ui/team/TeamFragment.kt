package com.ssafy.tmbg.ui.team

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.tmbg.R
import com.ssafy.tmbg.databinding.FragmentTeamBinding
import com.ssafy.tmbg.adapter.TeamAdapter
import com.ssafy.tmbg.ui.SharedViewModel
import com.ssafy.tmbg.ui.notice.NoticeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeamFragment : Fragment() {
    private var _binding: FragmentTeamBinding? = null
    private val binding get() = _binding!!
    private lateinit var teamAdapter: TeamAdapter
    private val viewModel: TeamViewModel by viewModels()
    private val noticeViewModel : NoticeViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()  // MainViewModel -> SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setupObservers()
        setupClickListeners()

        // SharedViewModel의 roomId Flow 수집
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.roomId.collect { roomId ->
                if (roomId != -1) {
                    viewModel.getTeam(roomId)
                }
            }
        }
    }

    private fun initRecyclerView() {
        teamAdapter = TeamAdapter(
            onTeamClick = { groupNumber ->
                viewModel.team.value?.let { team ->
                    val group = team.groups.find { it.groupNo == groupNumber }
                    val action = TeamFragmentDirections.actionTeamToTeamDetail(
                        groupNumber = groupNumber,
                        memberCount = group?.memberCount ?: 0
                    )
                    findNavController().navigate(action)
                }
            }
        )

        binding.recyclerView.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.btnShare.setOnClickListener {
            viewModel.shareInviteCode(requireContext())
        }

        binding.btnAdd.setOnClickListener {
            Log.d("TeamFragment", "그룹 추가 버튼 클릭")
            // collectLatest 대신 한 번만 값을 가져오는 방식으로 변경
            viewLifecycleOwner.lifecycleScope.launch {
                val roomId = sharedViewModel.roomId.value
                if (roomId != -1) {
                    Log.d("TeamFragment", "그룹 추가 시도 - roomId: $roomId")
                    viewModel.addGroup(roomId)
                } else {
                    Log.e("TeamFragment", "roomId가 -1입니다")
                }
            }
        }
        binding.btnFinishGrading.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_satisfaction_notice, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogView.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val roomId = sharedViewModel.roomId.value
                    if (roomId != -1) {
                        noticeViewModel.createSatisfactionNotice(roomId.toLong())
                    }
                }
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupObservers() {
        viewModel.team.observe(viewLifecycleOwner) { team ->
            team?.let {
                teamAdapter.updateData(it.numOfGroups.toInt())
                binding.btnShareCode.text = "초대 코드: ${it.inviteCode}"
                binding.tvRoomName.text = it.roomName
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}