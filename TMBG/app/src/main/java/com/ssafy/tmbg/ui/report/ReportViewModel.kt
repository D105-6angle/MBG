package com.ssafy.tmbg.ui.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tmbg.data.report.repositoy.ReportRepository
import com.ssafy.tmbg.data.report.repositoy.ReportRepositoryImpl
import com.ssafy.tmbg.ui.main.MainViewModel
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
    private val reportRepositoryImpl: ReportRepositoryImpl
) : ViewModel() {
    private val _state = MutableStateFlow<ReportState>(ReportState.Initial)
    val state: StateFlow<ReportState> = _state.asStateFlow()

    private var updateJob: Job? = null
    private var currentRoomId: Int = -1

    fun startAutoUpdate(roomId: Int) {
        Log.d("ReportViewModel", "startAutoUpdate called with roomId: $roomId")
        currentRoomId = roomId
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (true) {
                val result = fetchReport()
                Log.d("ReportViewModel", "fetchReport result: $result, currentRoomId: $currentRoomId")
                delay(10000)
            }
        }
    }

    fun clearRoomId() {
        Log.d("ReportViewModel", "clearRoomId called, previous roomId: $currentRoomId")
        currentRoomId = -1
        stopAutoUpdate()
        _state.value = ReportState.Initial
    }

    private suspend fun fetchReport(): Boolean {
        try {
            Log.d("ReportViewModel", "fetchReport started with currentRoomId: $currentRoomId")
            if (currentRoomId == -1) return false

            val result = reportRepositoryImpl.getReport(currentRoomId)

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

    override fun onCleared() {
        Log.d("ReportViewModel", "onCleared called with currentRoomId: $currentRoomId")
        super.onCleared()
        stopAutoUpdate()
    }

    private fun stopAutoUpdate() {
        Log.d("ReportViewModel", "stopAutoUpdate called")
        updateJob?.cancel()
        updateJob = null
    }
}