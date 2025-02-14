package com.ssafy.tmbg.ui.report

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ssafy.tmbg.adapter.AttendanceAdapter
import com.ssafy.tmbg.adapter.CommentAdapter
import com.ssafy.tmbg.data.report.Attendance
import com.ssafy.tmbg.data.report.SatisfactionData
import com.ssafy.tmbg.data.report.SatisfactionType
import com.ssafy.tmbg.data.report.dao.Satisfaction
import com.ssafy.tmbg.data.report.dao.Student
import com.ssafy.tmbg.data.report.response.ReportResponse
import com.ssafy.tmbg.databinding.FragmentTeamReportBinding
import com.ssafy.tmbg.ui.SharedViewModel
import com.ssafy.tmbg.util.ReportPdfGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 보고서 화면을 관리하는 Fragment
 * 학생들의 진행 상태와 최종 보고서를 표시함
 */
@AndroidEntryPoint
class ReportFragment : Fragment() {
    private var _binding: FragmentTeamReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var attendanceAdapter: AttendanceAdapter
    private lateinit var commentAdapter: CommentAdapter
    private val reportViewModel: ReportViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // PDF 생성에 필요한 데이터를 저장하는 변수들
    private lateinit var question1Data: List<SatisfactionData>
    private lateinit var question2Data: List<SatisfactionData>
    private lateinit var question3Data: List<SatisfactionData>
    private lateinit var roomName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            sharedViewModel.roomId.collect { roomId ->
                if (roomId != -1) {
                    reportViewModel.startAutoUpdate(roomId)
                }
            }
        }

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setupBackButton()
        setupDownloadButton()
        setupRecyclerView()
        setupCommentRecyclerView()
        binding.reportContainer.visibility = View.GONE
    }

    private fun setupCommentRecyclerView() {
        commentAdapter = CommentAdapter()
        binding.commentRecyclerView.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun loadComments(comments: List<String>) {
        commentAdapter.updateComments(comments)
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupDownloadButton() {
        binding.downloadButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("체험학습 종료")
                .setMessage("보고서를 저장하고 체험학습을 종료하시겠습니까?")
                .setPositiveButton("확인") { dialog, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            downloadReport()
                            reportViewModel.clearState()
                            sharedViewModel.clearRoomId()
                            dialog.dismiss()
                        } catch (e: Exception) {
                            Log.e("ReportFragment", "초기화 중 오류 발생", e)
                            Toast.makeText(context, "초기화 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setupRecyclerView() {
        attendanceAdapter = AttendanceAdapter()
        binding.studentListRecyclerView.apply {
            adapter = attendanceAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            reportViewModel.state.collect { state ->
                Log.d("ReportFragment", "State: $state")

                when (state) {
                    is ReportState.Success -> {
                        Log.d("ReportFragment", "State is Success")
                        Log.d("ReportFragment", "Completed: ${state.reportData.reports.completed}")
                        Log.d("ReportFragment", "Students: ${state.reportData.reports.students}")

                        if (!state.reportData.reports.completed) {
                            Log.d("ReportFragment", "Showing student list UI")
                            showStudentListUI(state.reportData)
                        } else {
                            Log.d("ReportFragment", "Showing report UI")
                            showReportUI(state.reportData)
                        }
                    }

                    is ReportState.Error -> {
                        Log.d("ReportFragment", "State is Error: ${state.message}")
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }

                    is ReportState.Loading -> {
                        Log.d("ReportFragment", "State is Loading")
                        binding.studentListContainer.visibility = View.GONE
                        binding.reportContainer.visibility = View.GONE
                    }

                    is ReportState.Initial -> {
                        Log.d("ReportFragment", "State is Initial")
                        binding.studentListContainer.visibility = View.GONE
                        binding.reportContainer.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showReportUI(reportData: ReportResponse) {
        binding.apply {
            studentListContainer.visibility = View.GONE
            reportContainer.visibility = View.VISIBLE

            // 초기 상태 설정 - 모든 요소 투명하게
            classInfoCard.alpha = 0f
            titlteText.alpha = 0f
            downloadButton.alpha = 0f
            attendanceRecyclerView.alpha = 0f
            Question1donutChart.alpha = 0f
            Question2donutChart.alpha = 0f
            Question3donutChart.alpha = 0f
            commentRecyclerView.alpha = 0f

            // 기본 데이터 설정
            titlteText.text = reportData.reports.roomName
            roomName = reportData.reports.roomName

            // 카드 페이드인 애니메이션
            classInfoCard.animate()
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(DecelerateInterpolator())
                .start()

            // 각 요소 순차적 애니메이션
            val elements = listOf(
                titlteText,
                downloadButton,
                attendanceRecyclerView,
                Question1donutChart,
                Question2donutChart,
                Question3donutChart,
                commentRecyclerView
            )

            elements.forEachIndexed { index, view ->
                view.translationY = 50f
                view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay(100L * index)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }

            // 데이터 설정
            val students = reportData.reports.students ?: emptyList()

            attendanceRecyclerView.apply {
                adapter = attendanceAdapter
                layoutManager = LinearLayoutManager(context)
            }
            attendanceAdapter.updateAttendance(students.map { Attendance(it.name) })

            // 만족도 차트와 출석 데이터 설정
            setupSatisfactionCharts(reportData.reports.reportData.satisfaction)
            loadAttendanceData(reportData.reports.students)
            loadComments(reportData.reports.reportData.comments)
        }
    }

    private fun showStudentListUI(reportData: ReportResponse) {
        Log.d("ReportFragment", "showStudentListUI called")
        Log.d("ReportFragment", "students: ${reportData.reports.students}")

        binding.apply {
            studentListContainer.visibility = View.VISIBLE
            reportContainer.visibility = View.GONE

            // 방 이름 설정
            titlteText.text = reportData.reports.roomName
            roomName = reportData.reports.roomName // PDF 생성용 변수 저장

            // 학생 목록 표시
            val students = reportData.reports.students ?: emptyList()
            Log.d(
                "ReportFragment",
                "Mapping students to attendance: ${students.map { Attendance(it.name) }}"
            )

            studentListRecyclerView.apply {
                adapter = attendanceAdapter
                layoutManager = LinearLayoutManager(context)
            }
            attendanceAdapter.updateAttendance(
                students.map { Attendance(it.name) }
            )
        }
    }

    private fun setupSatisfactionCharts(satisfactionList: List<Satisfaction>) {
        question1Data = satisfactionList[0].toSatisfactionDataList()
        question2Data = satisfactionList[1].toSatisfactionDataList()
        question3Data = satisfactionList[2].toSatisfactionDataList()

        with(binding) {
            // 문항 1 차트 설정
            Question1donutChart.setData(question1Data)
            Question1legendVeryGood.setData(question1Data[0])
            Question1legendGood.setData(question1Data[1])
            Question1legendNormal.setData(question1Data[2])
            Question1legendBad.setData(question1Data[3])
            Question1legendVeryBad.setData(question1Data[4])

            // 문항 2 차트 설정
            Question2donutChart.setData(question2Data)
            Question2legendVeryGood.setData(question2Data[0])
            Question2legendGood.setData(question2Data[1])
            Question2legendNormal.setData(question2Data[2])
            Question2legendBad.setData(question2Data[3])
            Question2legendVeryBad.setData(question2Data[4])

            // 문항 3 차트 설정
            Question3donutChart.setData(question3Data)
            Question3legendVeryGood.setData(question3Data[0])
            Question3legendGood.setData(question3Data[1])
            Question3legendNormal.setData(question3Data[2])
            Question3legendBad.setData(question3Data[3])
            Question3legendVeryBad.setData(question3Data[4])
        }
    }

    private fun loadAttendanceData(students: List<Student>) {
        binding.studentListRecyclerView.apply {
            if (adapter == null) {
                adapter = attendanceAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
        attendanceAdapter.updateAttendance(
            students.map { Attendance(it.name) }
        )
    }

    private fun Satisfaction.toSatisfactionDataList(): List<SatisfactionData> {
        return listOf(
            SatisfactionData(SatisfactionType.VERY_GOOD, veryGood_rate.toFloat() * 100),
            SatisfactionData(SatisfactionType.GOOD, good_rate.toFloat()* 100),
            SatisfactionData(SatisfactionType.NORMAL, neutral_rate.toFloat()* 100),
            SatisfactionData(SatisfactionType.BAD, bad_rate.toFloat()* 100),
            SatisfactionData(SatisfactionType.VERY_BAD, veryBad_rate.toFloat()* 100)
        )
    }

    private fun downloadReport() {
        try {
            val pdfGenerator = ReportPdfGenerator(requireContext())

            val file = pdfGenerator.generatePdf(
                className = roomName,
                question1Data = question1Data,
                question2Data = question2Data,
                question3Data = question3Data,
                studentList = attendanceAdapter.getCurrentList().map { it.name },
                charts = listOf(
                    binding.Question1donutChart,
                    binding.Question2donutChart,
                    binding.Question3donutChart
                ),
                comments = commentAdapter.getCurrentList()
            )

            // PDF 파일을 공유하기 위한 FileProvider URI 생성
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )

            // PDF 파일을 열기 위한 인텐트 설정
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            startActivity(Intent.createChooser(intent, "PDF 파일 열기"))
            Toast.makeText(context, "PDF 파일이 생성되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "PDF 생성 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}