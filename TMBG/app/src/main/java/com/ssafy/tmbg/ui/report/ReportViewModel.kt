package com.ssafy.tmbg.ui.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tmbg.data.report.repositoy.ReportRepository
import com.ssafy.tmbg.data.report.repositoy.ReportRepositoryImpl
import com.ssafy.tmbg.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 보고서 관련 데이터를 관리하는 ViewModel
 * 15초마다 자동으로 보고서 데이터를 갱신하고 상태를 관리함
 */
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepositoryImpl: ReportRepositoryImpl,
) : ViewModel() {
    private val _state = MutableStateFlow<ReportState>(ReportState.Initial)
    val state: StateFlow<ReportState> = _state.asStateFlow()

    private var updateJob: Job? = null

    fun startAutoUpdate(roomId: Int) {
        Log.d("ReportViewModel", "startAutoUpdate: $roomId")
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (true) {
                fetchReport(roomId)
                delay(10000)
            }
        }
    }

    private suspend fun fetchReport(roomId: Int): Boolean {
        if (roomId == -1) return false

        try {
            val result = reportRepositoryImpl.getReport(roomId)
            result.onSuccess { response ->
                _state.value = ReportState.Success(
                    message = "보고서가 업데이트 되었습니다.",
                    isCompleted = response.reports.completed,
                    reportData = response
                )
                return response.reports.completed
            }.onFailure { error ->
                if (_state.value !is ReportState.Success) {
                    _state.value = ReportState.Error(error.message ?: "알수 없는 오류가 발생했어요")
                }
            }
        } catch (e: Exception) {
            Log.e("ReportViewModel", "Error in fetchReport", e)
            if (_state.value !is ReportState.Success) {
                _state.value = ReportState.Error(e.message ?: "알 수 없는 오류가 발생했어요")
            }
        }
        return false
    }

    fun clearState() {
        stopAutoUpdate()
        _state.value = ReportState.Initial
    }

    private fun stopAutoUpdate() {
        Log.d("ReportViewModel", "stopAutoUpdate called")
        updateJob?.cancel()
        updateJob = null
    }

    override fun onCleared() {
        Log.d("ReportViewModel", "onCleared called")
        super.onCleared()
        stopAutoUpdate()
    }
}