package com.ssafy.tmbg.ui.report

import com.ssafy.tmbg.data.report.response.ReportResponse

sealed class ReportState{
    data object Initial : ReportState()
    data object Loading : ReportState()
    data class Success(
        val message: String,
        val isCompleted : Boolean,
        val reportData : ReportResponse
        ) : ReportState()
    data class Error(val message: String) : ReportState()
}