package com.ssafy.mbg.ui.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.mbg.data.report.repository.ReportRepository
import com.ssafy.mbg.data.report.request.ReportRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SatisfactionViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<SatisfactionUiState>()
    val uiState: LiveData<SatisfactionUiState> = _uiState

    fun submitSurvey(
        roomId: Long,
        userId: Long,
        answer1: String?,
        answer2: String?,
        answer3: String?,
        answer4: String
    ) {
        if (answer1 == null || answer2 == null || answer3 == null) {
            _uiState.value = SatisfactionUiState.Error("모든 문항에 답변해주세요.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SatisfactionUiState.Loading

            val reportRequest = ReportRequest(
                roomId = roomId,
                userId = userId,
                no1 = convertAnswerToNumber(answer1),
                no2 = convertAnswerToNumber(answer2),
                no3 = convertAnswerToNumber(answer3),
                no4 = answer4
            )

            reportRepository.createReport(reportRequest).fold(
                onSuccess = {
                    _uiState.value = SatisfactionUiState.Success
                },
                onFailure = { exception ->
                    _uiState.value = SatisfactionUiState.Error(
                        exception.message ?: "알 수 없는 오류가 발생했습니다."
                    )
                }
            )
        }
    }

    private fun convertAnswerToNumber(answer: String): Int {
        return when (answer) {
            "매우 좋음" -> 5
            "좋음" -> 4
            "보통" -> 3
            "나쁨" -> 2
            "매우 나쁨" -> 1
            else -> 0
        }
    }
}