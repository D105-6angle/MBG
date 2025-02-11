//package com.ssafy.tmbg.ui.report
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.core.content.FileProvider
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.ssafy.tmbg.adapter.AttendanceAdapter
//import com.ssafy.tmbg.data.report.Attendance
//import com.ssafy.tmbg.data.report.SatisfactionData
//import com.ssafy.tmbg.data.report.SatisfactionType
//import com.ssafy.tmbg.data.report.dao.Satisfaction
//import com.ssafy.tmbg.data.report.dao.Student
//import com.ssafy.tmbg.data.report.response.ReportResponse
//import com.ssafy.tmbg.databinding.FragmentTeamReportBinding
//import com.ssafy.tmbg.util.ReportPdfGenerator
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.launch
//
///**
// * 보고서 화면을 관리하는 Fragment
// * 학생들의 진행 상태와 최종 보고서를 표시함
// */
//@AndroidEntryPoint
//class TempReportFragment : Fragment() {
//    private var _binding: FragmentTeamReportBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var attendanceAdapter: AttendanceAdapter
//    private val reportViewModel: ReportViewModel by viewModels()
//
//    // PDF 생성에 필요한 데이터를 저장하는 변수들
//    private lateinit var question1Data: List<SatisfactionData>
//    private lateinit var question2Data: List<SatisfactionData>
//    private lateinit var question3Data: List<SatisfactionData>
//    private lateinit var roomName: String
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentTeamReportBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setupUI()
//        observeViewModel()
//        reportViewModel.startAutoUpdate() // 자동 업데이트 시작
//    }
//
//    /**
//     * UI 초기 설정을 담당하는 함수
//     */
//    private fun setupUI() {
//        setupBackButton()
//        setupDownloadButton()
//        setupRecyclerView()
//        binding.reportContainer.visibility = View.GONE // 초기에는 보고서 숨김
//    }
//
//    /**
//     * 뒤로가기 버튼 설정
//     */
//    private fun setupBackButton() {
//        binding.backButton.setOnClickListener {
//            activity?.onBackPressed()
//        }
//    }
//
//    /**
//     * PDF 다운로드 버튼 설정
//     */
//    private fun setupDownloadButton() {
//        binding.downloadButton.setOnClickListener {
//            downloadReport()
//        }
//    }
//
//    /**
//     * RecyclerView 초기 설정
//     */
//    private fun setupRecyclerView() {
//        attendanceAdapter = AttendanceAdapter()
//        binding.attendanceRecyclerView.apply {
//            adapter = attendanceAdapter
//            layoutManager = LinearLayoutManager(context)
//            setHasFixedSize(true)
//        }
//    }
//
//    /**
//     * ViewModel의 상태를 관찰하고 UI를 업데이트하는 함수
//     */
//    private fun observeViewModel() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            reportViewModel.state.collect { state ->
//                when(state) {
//                    is ReportState.Success -> {
//                        if (state.isCompleted) {
//                            showReportUI(state.reportData) // 보고서 완료 시 보고서 UI 표시
//                        } else {
//                            showStudentListUI(state.reportData.students) // 진행 중일 때 학생 목록 표시
//                        }
//                    }
//                    is ReportState.Error -> {
//                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
//                    }
//                    else -> {
//                        // Initial, Loading 상태 처리
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 완료된 보고서 UI를 표시하는 함수
//     * @param reportData 서버에서 받아온 보고서 데이터
//     */
//    private fun showReportUI(reportData: ReportResponse) {
//        binding.apply {
//            reportContainer.visibility = View.VISIBLE
//            studentListContainer.visibility = View.GONE
//
//            roomName = reportData.roomName
//            titlteText.text = roomName
//
//            setupSatisfactionCharts(reportData.reportData.satisfaction)
//            loadAttendanceData(reportData.students)
//        }
//    }
//
//    /**
//     * 진행 중인 학생 목록 UI를 표시하는 함수
//     * @param students 현재 참여 중인 학생 목록
//     */
//    private fun showStudentListUI(students: List<Student>) {
//        binding.apply {
//            reportContainer.visibility = View.GONE
//            studentListContainer.visibility = View.VISIBLE
//
//            attendanceAdapter.updateAttendance(
//                students.map { Attendance(it.name) }
//            )
//        }
//    }
//
//    /**
//     * 만족도 차트를 설정하는 함수
//     * @param satisfactionList 각 문항별 만족도 데이터 리스트
//     */
//    private fun setupSatisfactionCharts(satisfactionList: List<Satisfaction>) {
//        question1Data = satisfactionList[0].toSatisfactionDataList()
//        question2Data = satisfactionList[1].toSatisfactionDataList()
//        question3Data = satisfactionList[2].toSatisfactionDataList()
//
//        with(binding) {
//            // 문항 1 차트 설정
//            Question1donutChart.setData(question1Data)
//            Question1legendVeryGood.setData(question1Data[0])
//            Question1legendGood.setData(question1Data[1])
//            Question1legendNormal.setData(question1Data[2])
//            Question1legendBad.setData(question1Data[3])
//            Question1legendVeryBad.setData(question1Data[4])
//
//            // 문항 2 차트 설정
//            Question2donutChart.setData(question2Data)
//            Question2legendVeryGood.setData(question2Data[0])
//            Question2legendGood.setData(question2Data[1])
//            Question2legendNormal.setData(question2Data[2])
//            Question2legendBad.setData(question2Data[3])
//            Question2legendVeryBad.setData(question2Data[4])
//
//            // 문항 3 차트 설정
//            Question3donutChart.setData(question3Data)
//            Question3legendVeryGood.setData(question3Data[0])
//            Question3legendGood.setData(question3Data[1])
//            Question3legendNormal.setData(question3Data[2])
//            Question3legendBad.setData(question3Data[3])
//            Question3legendVeryBad.setData(question3Data[4])
//        }
//    }
//
//    /**
//     * 출석 데이터를 RecyclerView에 로드하는 함수
//     * @param students 학생 목록
//     */
//    private fun loadAttendanceData(students: List<Student>) {
//        attendanceAdapter.updateAttendance(
//            students.map { Attendance(it.name) }
//        )
//    }
//
//    /**
//     * Satisfaction 객체를 SatisfactionData 리스트로 변환하는 확장 함수
//     * @return 변환된 SatisfactionData 리스트
//     */
//    private fun Satisfaction.toSatisfactionDataList(): List<SatisfactionData> {
//        return listOf(
//            SatisfactionData(SatisfactionType.VERY_GOOD, veryGoodRate.toFloat()),
//            SatisfactionData(SatisfactionType.GOOD, goodRateRate.toFloat()),
//            SatisfactionData(SatisfactionType.NORMAL, neutralRate.toFloat()),
//            SatisfactionData(SatisfactionType.BAD, badRate.toFloat()),
//            SatisfactionData(SatisfactionType.VERY_BAD, veryBadRate.toFloat())
//        )
//    }
//
//    /**
//     * 보고서를 PDF로 다운로드하는 함수
//     */
//    private fun downloadReport() {
//        try {
//            val pdfGenerator = ReportPdfGenerator(requireContext())
//
//            val file = pdfGenerator.generatePdf(
//                className = roomName,
//                location = "경복궁",  // 임시 하드코딩 값
//                question1Data = question1Data,
//                question2Data = question2Data,
//                question3Data = question3Data,
//                studentList = attendanceAdapter.getCurrentList().map { it.studentName },
//                charts = listOf(
//                    binding.Question1donutChart,
//                    binding.Question2donutChart,
//                    binding.Question3donutChart
//                )
//            )
//
//            // PDF 파일을 공유하기 위한 FileProvider URI 생성
//            val uri = FileProvider.getUriForFile(
//                requireContext(),
//                "${requireContext().packageName}.provider",
//                file
//            )
//
//            // PDF 파일을 열기 위한 인텐트 설정
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                setDataAndType(uri, "application/pdf")
//                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            }
//
//            startActivity(Intent.createChooser(intent, "PDF 파일 열기"))
//            Toast.makeText(context, "PDF 파일이 생성되었습니다.", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Toast.makeText(context, "PDF 생성 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * Fragment가 제거될 때 바인딩 객체 정리
//     */
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}