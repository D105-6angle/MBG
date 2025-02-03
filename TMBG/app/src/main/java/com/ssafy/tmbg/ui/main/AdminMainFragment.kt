package com.ssafy.tmbg.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ssafy.tmbg.R
import com.ssafy.tmbg.databinding.FragmentAdminMainBinding
import com.ssafy.tmbg.ui.team.TeamCreateDialog

class AdminMainFragment : Fragment() {
    private var _binding: FragmentAdminMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnTeam.setOnClickListener {
                TeamCreateDialog().show(childFragmentManager, "TeamCreateDialog")
            }
            btnNotice.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_notice)
            }
            btnMission.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_mission)
            }
            btnSchedule.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_schedule)
            }
            btnReport.setOnClickListener {
                findNavController().navigate(R.id.action_adminMain_to_report)
            }
        }
    }

    fun navigateToTeam() {
        findNavController().navigate(R.id.action_adminMain_to_team)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}