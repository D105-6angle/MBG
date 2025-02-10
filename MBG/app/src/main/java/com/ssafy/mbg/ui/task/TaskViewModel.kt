package com.ssafy.mbg.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.mbg.data.task.dao.Schedule
import com.ssafy.mbg.data.task.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {
    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> = _schedules

    private val _state = MutableLiveData<ScheduleState>(ScheduleState.Initial)
    val state: LiveData<ScheduleState> = _state

    fun getSchedules(roomId: Long) {
        viewModelScope.launch {
            try {
                _state.value = ScheduleState.Loading
                val result = repository.getSchedules(roomId)

                if (result.isSuccessful) {
                    val body = result.body()
                    if (body != null && body.schedules.isNotEmpty()) {
                        _schedules.value = body.schedules
                        _state.value = ScheduleState.Success(body.schedules)  // 전체 스케줄 리스트 전달
                    } else {
                        _state.value = ScheduleState.Error("일정이 없습니다.")
                        _schedules.value = emptyList()
                    }
                } else {
                    val errorMessage = result.errorBody()?.string() ?: "알 수 없는 오류가 발생했습니다."
                    _state.value = ScheduleState.Error(errorMessage)
                    _schedules.value = emptyList()
                }
            } catch (e: Exception) {
                _state.value = ScheduleState.Error(e.message ?: "네트워크 오류가 발생했습니다.")
                _schedules.value = emptyList()
            }
        }
    }
}