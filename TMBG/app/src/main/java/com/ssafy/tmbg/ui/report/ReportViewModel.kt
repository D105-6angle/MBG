package com.ssafy.tmbg.ui.report

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
    private val reportRepositoryImpl: ReportRepositoryImpl,
    private val mainViewModel: MainViewModel
) : ViewModel() {
    // 현재 보고서 상태를 담는 StateFlow
    private val _state = MutableStateFlow<ReportState>(ReportState.Initial)
    val state: StateFlow<ReportState> = _state.asStateFlow()

    // 자동 갱신을 위한 코루틴 Job
    private var updateJob: Job? = null

    /**
     * 자동 갱신을 시작하는 함수
     * 이전 갱신 작업이 있다면 취소하고 새로운 갱신 작업을 시작함
     */
    fun startAutoUpdate() {
        updateJob?.cancel() // 기존 작업이 있다면 취소
        updateJob = viewModelScope.launch {
            while (true) {
                fetchReport()
                delay(10000) // 10초 대기
            }
        }
    }

    /**
     * 자동 갱신을 중지하는 함수
     */
    private fun stopAutoUpdate() {
        updateJob?.cancel()
        updateJob = null
    }

    /**
     * 보고서 데이터를 가져오는 함수
     * @return Boolean 보고서 완료 여부 (isCompleted)
     */
    private suspend fun fetchReport(): Boolean {
        try {
            // MainViewModel에서 roomId 가져오기
            val roomId = mainViewModel.roomId.value ?: -1
            val result = reportRepositoryImpl.getReport(roomId)

            result.onSuccess { response ->
                // 성공적으로 데이터를 가져온 경우
                _state.value = ReportState.Success(
                    message = "보고서가 업데이트 되었습니다.",
                    isCompleted = response.isCompleted,
                    reportData = response
                )
                return response.isCompleted
            }.onFailure { error ->
                // 에러가 발생했고, 이전에 성공 상태가 아니었던 경우에만 에러 상태로 변경
                if (_state.value !is ReportState.Success) {
                    _state.value = ReportState.Error(error.message ?: "알수 없는 오류가 발생했어요")
                }
            }
        } catch (e: Exception) {
            // 예외가 발생했고, 이전에 성공 상태가 아니었던 경우에만 에러 상태로 변경
            if (_state.value !is ReportState.Success) {
                _state.value = ReportState.Error(e.message ?: "알 수 없는 오류가 발생 했어요")
            }
        }
        return false
    }

    /**
     * ViewModel이 제거될 때 호출되는 함수
     * 자동 갱신을 중지시킴
     */
    override fun onCleared() {
        super.onCleared()
        stopAutoUpdate()
    }
}