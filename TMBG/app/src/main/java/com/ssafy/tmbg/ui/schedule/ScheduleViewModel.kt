package com.ssafy.tmbg.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tmbg.data.schedule.Schedule
import com.ssafy.tmbg.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> = _schedules

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getSchedules(roomId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getSchedules(roomId)
                if (response.isSuccessful) {
                    _schedules.value = response.body()
                } else {
                    _error.value = "스케줄을 불러오는데 실패했습니다."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }
} 