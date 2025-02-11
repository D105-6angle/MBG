package com.ssafy.tmbg.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ssafy.tmbg.adapter.AttendanceAdapter
import com.ssafy.tmbg.data.report.Attendance
import com.ssafy.tmbg.data.report.dao.ReportData
import com.ssafy.tmbg.data.report.dao.Student
import com.ssafy.tmbg.data.report.response.ReportResponse
import com.ssafy.tmbg.databinding.FragmentReportBinding
import com.ssafy.tmbg.databinding.FragmentTemReportBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TempReportFragment : Fragment() {
    private var _binding : FragmentTemReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val reportViewModel : ReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
        reportViewModel.startAutoUpdate()
    }

    private fun setupUI() {
        setupBackButton()
        setupDownloadButton()
        setupRecyclerView()

        binding.reportContainer.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            reportViewModel.state.collect { state ->
                when(state) {
                    is ReportState.Success -> {
                        if (state.isCompleted) {
                            showReportUI(state.reportData)
                        } else {
                            showStudentListUI(state.reportData.students)
                        }
                    }
                    is ReportState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun showReportUI(reportData: ReportResponse) {
        binding.apply {
            reportContainer.visibility = View.VISIBLE
            studentListContainer.visibility = View.GONE

            titlteText.text = reportData.roomName

            setupSatisfactionCharts(reportData.reportData.satisfaction)

            loadAttendanceData(reportData.students)

        }
    }

    private fun showStudentListUI(students: List<Student>) {
        binding.apply {
            reportContainer.visibility = View.GONE
            studentListContainer.visibility = View.VISIBLE

            attendanceAdapter.updateAttendance(
                students.map { Attendance(it.name) }
            )
        }
    }

    private fun setupSatisfactionCharts
}