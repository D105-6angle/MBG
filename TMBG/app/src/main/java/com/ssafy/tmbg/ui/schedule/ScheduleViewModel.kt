package com.ssafy.tmbg.ui.schedule

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tmbg.data.schedule.dao.Schedule
import com.ssafy.tmbg.data.schedule.dao.ScheduleRequest
import com.ssafy.tmbg.data.schedule.dao.ScheduleUpdateRequest
import com.ssafy.tmbg.data.schedule.repository.ScheduleRepository
import com.ssafy.tmbg.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 일정 관리 화면의 비즈니스 로직을 담당하는 ViewModel
 * Repository를 통해 서버와 통신하고 UI 상태를 관리합니다.
 */
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
) : ViewModel() {

    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> = _schedules

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun getSchedules(roomId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("ScheduleViewModel", "Requesting schedules for roomId: $roomId")
                val response = repository.getSchedules(roomId)

                Log.d("ScheduleViewModel", "Response code: ${response.code()}")
                Log.d("ScheduleViewModel", "Response headers: ${response.headers()}")
                Log.d("ScheduleViewModel", "Response raw: ${response.raw()}")

                if (response.isSuccessful) {
                    val scheduleResponse = response.body()
                    Log.d("ScheduleViewModel", "Response body: $scheduleResponse")

                    scheduleResponse?.let {
                        Log.d("ScheduleViewModel", "Parsed schedules: ${it.schedules}")
                        _schedules.value = it.schedules
                    } ?: run {
                        Log.e("ScheduleViewModel", "Response body is null")
                        _schedules.value = emptyList()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ScheduleViewModel", "Error response: $errorBody")
                    Log.e("ScheduleViewModel", "Error code: ${response.code()}")
                    _error.value = "스케줄을 불러오는데 실패했습니다. (${response.code()})"
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Exception while fetching schedules", e)
                _error.value = "네트워크 오류: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createSchedule(roomId: Int, scheduleRequest: ScheduleRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("ScheduleViewModel", "Creating schedule for roomId: $roomId")
                Log.d("ScheduleViewModel", "Request body: $scheduleRequest")

                val response = repository.createSchedule(roomId, scheduleRequest)

                Log.d("ScheduleViewModel", "Response code: ${response.code()}")
                Log.d("ScheduleViewModel", "Response headers: ${response.headers()}")
                Log.d("ScheduleViewModel", "Response raw: ${response.raw()}")

                if (response.isSuccessful) {
                    val newSchedule = response.body()
                    Log.d("ScheduleViewModel", "Response body (new schedule): $newSchedule")

                    val currentList = _schedules.value.orEmpty().toMutableList()
                    newSchedule?.let {
                        currentList.add(it)
                        _schedules.value = currentList
                        Log.d("ScheduleViewModel", "Schedule created successfully")
                    } ?: run {
                        Log.e("ScheduleViewModel", "Response body is null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ScheduleViewModel", "Error response: $errorBody")
                    Log.e("ScheduleViewModel", "Error code: ${response.code()}")
                    _error.value = "일정 생성에 실패했습니다. (${response.code()})"
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Exception while creating schedule", e)
                _error.value = "네트워크 오류: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSchedule(roomId: Int, scheduleId: Int, schedule: Schedule) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("ScheduleViewModel", "Updating schedule - roomId: $roomId, scheduleId: $scheduleId")
                Log.d("ScheduleViewModel", "Update request body: $schedule")

                val updateRequest = ScheduleUpdateRequest(
                    startTime = schedule.startTime,
                    endTime = schedule.endTime,
                    content = schedule.content
                )

                val response = repository.updateSchedule(roomId, scheduleId, updateRequest)

                Log.d("ScheduleViewModel", "Response code: ${response.code()}")
                Log.d("ScheduleViewModel", "Response headers: ${response.headers()}")
                Log.d("ScheduleViewModel", "Response raw: ${response.raw()}")

                if (response.isSuccessful) {
                    val updatedSchedule = response.body()
                    Log.d("ScheduleViewModel", "Response body (updated schedule): $updatedSchedule")

                    val currentList = _schedules.value.orEmpty().toMutableList()
                    val index = currentList.indexOfFirst { it.scheduleId == scheduleId }
                    if (index != -1 && updatedSchedule != null) {
                        currentList[index] = updatedSchedule
                        _schedules.value = currentList
                        Log.d("ScheduleViewModel", "Schedule updated successfully")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ScheduleViewModel", "Error response: $errorBody")
                    Log.e("ScheduleViewModel", "Error code: ${response.code()}")
                    _error.value = "일정 수정에 실패했습니다. (${response.code()})"
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Exception while updating schedule", e)
                _error.value = "네트워크 오류: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSchedule(roomId: Int, scheduleId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.deleteSchedule(roomId, scheduleId)
                if (response.isSuccessful) {
                    val currentList = _schedules.value.orEmpty().toMutableList()
                    currentList.removeAll { it.scheduleId == scheduleId }
                    _schedules.value = currentList
                } else {
                    _error.value = "일정 삭제에 실패했습니다."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }
}