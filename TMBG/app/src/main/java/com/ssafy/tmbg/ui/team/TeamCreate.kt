package com.ssafy.tmbg.ui.team

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import com.ssafy.tmbg.databinding.CreateTeamBinding
import com.ssafy.tmbg.data.team.dao.TeamRequest
import com.ssafy.tmbg.ui.main.AdminMainFragment
import dagger.hilt.android.AndroidEntryPoint
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.ssafy.tmbg.R
import com.ssafy.tmbg.ui.SharedViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeamCreateDialog : DialogFragment() {
    private var _binding: CreateTeamBinding? = null
    private val binding get() = _binding!!
    private val teamViewModel: TeamViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupLocationDropdown()
        setupSubmitButton()
    }

    private fun setupObservers() {
        // TeamViewModel의 상태 관찰
        teamViewModel.team.observe(viewLifecycleOwner) { team ->
            team?.let {
                Log.d("TeamCreateDialog", "Team created successfully: $team")
                Log.d("TeamCreateDialog", "parentFragment: $parentFragment")
                dismiss()
                val adminMainFragment = parentFragment as? AdminMainFragment
                Log.d("TeamCreateDialog", "adminMainFragment: $adminMainFragment")
                adminMainFragment?.navigateToTeam() ?: run {
                    Log.e("TeamCreateDialog", "Failed to cast parentFragment to AdminMainFragment")
                }
            }
        }

        teamViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupLocationDropdown() {
        val locations = arrayOf(
            "경복궁",
            "인동향교",
            // 더 많은 문화재 추가
        )

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            locations
        ).apply {
            setNotifyOnChange(true)
        }

        binding.locationDropdown.apply {
            setAdapter(adapter)
            threshold = 1  // 1글자 입력부터 필터링 시작
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    showDropDown()  // 포커스 받으면 드롭다운 표시
                }
            }
        }
    }

    private fun setupSubmitButton() {
        binding.submitButton.setOnClickListener {
            val teamName = binding.editText2.text.toString()
            val location = binding.locationDropdown.text.toString()
            val numOfGroups = binding.editText3.text.toString().toIntOrNull() ?: 1

            if (teamName.isNotEmpty() && location.isNotEmpty()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    teamViewModel.createTeam(TeamRequest(
                        roomName = teamName,
                        location = location,
                        numOfGroups = numOfGroups
                    )).onSuccess { newRoomId ->
                        sharedViewModel.setRoomId(newRoomId)
                    }
                }
            } else {
                Toast.makeText(context, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            val displayMetrics = requireContext().resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.8).toInt()
            val height = (displayMetrics.heightPixels * 0.65).toInt()

            setLayout(width, height)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}