package com.ssafy.tmbg.ui.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.tmbg.adapter.AttendanceAdapter
import com.ssafy.tmbg.data.report.Attendance
import com.ssafy.tmbg.data.report.SatisfactionData
import com.ssafy.tmbg.data.report.SatisfactionType
import com.ssafy.tmbg.databinding.FragmentReportBinding
import com.ssafy.tmbg.util.ReportPdfGenerator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var attendanceAdapter: AttendanceAdapter

    private lateinit var question1Data : List<SatisfactionData>
    private lateinit var question2Data : List<SatisfactionData>
    private lateinit var question3Data : List<SatisfactionData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadData()
    }

    private fun setupUI() {
        setupBackButton()
        setupDownloadButton()
        setupRecyclerView()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupDownloadButton() {
        binding.downloadButton.setOnClickListener {
            // TODO: 다운로드 기능 구현
            downloadReport()
        }
    }

    private fun setupRecyclerView() {
        attendanceAdapter = AttendanceAdapter()
        binding.attendanceRecyclerView.apply {
            adapter = attendanceAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun loadData() {
        setupSatisfactionCharts()
        loadAttendanceData()
    }

    private fun setupSatisfactionCharts() {
        // 문항 1 데이터
        question1Data = listOf(
            SatisfactionData(SatisfactionType.VERY_GOOD, 33.11f),
            SatisfactionData(SatisfactionType.GOOD, 20.02f),
            SatisfactionData(SatisfactionType.NORMAL, 30.13f),
            SatisfactionData(SatisfactionType.BAD, 10.13f),
            SatisfactionData(SatisfactionType.VERY_BAD, 5.13f),
        )

        // 문항 2 데이터
        question2Data = listOf(
            SatisfactionData(SatisfactionType.VERY_GOOD, 35.5f),
            SatisfactionData(SatisfactionType.GOOD, 30.2f),
            SatisfactionData(SatisfactionType.NORMAL, 25.3f),
            SatisfactionData(SatisfactionType.BAD, 10.13f),
            SatisfactionData(SatisfactionType.VERY_BAD, 5.13f),
        )

        // 문항 3 데이터
        question3Data = listOf(
            SatisfactionData(SatisfactionType.VERY_GOOD, 42.3f),
            SatisfactionData(SatisfactionType.GOOD, 31.5f),
            SatisfactionData(SatisfactionType.NORMAL, 26.2f),
            SatisfactionData(SatisfactionType.BAD, 10.13f),
            SatisfactionData(SatisfactionType.VERY_BAD, 5.13f),
        )

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

    private fun loadAttendanceData() {
        // 더미 데이터
        val attendanceList = listOf(
            Attendance("김학생"),
            Attendance("이학생"),
            Attendance("박학생"),
            Attendance("최학생")
        )
        attendanceAdapter.updateAttendance(attendanceList)
    }

    private fun downloadReport() {
        try {
            val pdfGenerator = ReportPdfGenerator(requireContext())

            // 현재 표시된 데이터로 PDF 생성
            val file = pdfGenerator.generatePdf(
                className = "구미 초등학교 1반",
                location = "경북궁",
                question1Data = question1Data,
                question2Data = question2Data,
                question3Data = question3Data,
                studentList = attendanceAdapter.getCurrentList().map { it.studentName },
                charts = listOf(
                    binding.Question1donutChart,
                    binding.Question2donutChart,
                    binding.Question3donutChart
                )
            )

            // PDF 생성 완료 후 공유 인텐트 실행
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )

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