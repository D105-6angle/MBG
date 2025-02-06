package com.ssafy.tmbg.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tmbg.data.schedule.dao.Schedule
import com.ssafy.tmbg.data.schedule.dao.ScheduleRequest
import com.ssafy.tmbg.data.schedule.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 일정 관리 화면의 비즈니스 로직을 담당하는 ViewModel
 * Repository를 통해 서버와 통신하고 UI 상태를 관리합니다.
 */
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository
) : ViewModel() {

    // UI에 표시될 일정 목록을 관리하는 LiveData
    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> = _schedules  // 외부에서는 읽기 전용으로 제공

    // API 호출 중 로딩 상태를 관리하는 LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 에러 메시지를 관리하는 LiveData
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * 일정 목록을 서버에서 조회합니다.
     * @param roomId 방 ID
     * 
     * 동작 과정:
     * 1. 로딩 상태를 true로 설정
     * 2. API 호출 시도
     * 3. 성공 시: schedules LiveData를 새로운 목록으로 업데이트
     * 4. 실패 시: error LiveData에 에러 메시지 설정
     * 5. 완료 시: 로딩 상태를 false로 설정
     */
    fun getSchedules(roomId: Long) {
        viewModelScope.launch {
            _isLoading.value = true  // 로딩 시작
            try {
                val response = repository.getSchedules(roomId)
                if (response.isSuccessful) {
                    _schedules.value = response.body()  // 성공 시 목록 업데이트
                } else {
                    _error.value = "스케줄을 불러오는데 실패했습니다."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다."
            } finally {
                _isLoading.value = false  // 로딩 종료
            }
        }
    }

    /**
     * 새로운 일정을 생성합니다.
     * @param roomId 방 ID
     * @param scheduleRequest 생성할 일정 정보
     * 
     * 동작 과정:
     * 1. 로딩 상태를 true로 설정
     * 2. API 호출하여 일정 생성 시도
     * 3. 성공 시: 
     *    - 서버에서 받은 새 일정을 현재 목록에 추가
     *    - schedules LiveData 업데이트
     * 4. 실패 시: error LiveData에 에러 메시지 설정
     * 5. 완료 시: 로딩 상태를 false로 설정
     */
    fun createSchedule(roomId: Long, scheduleRequest: ScheduleRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.createSchedule(roomId, scheduleRequest)
                if (response.isSuccessful) {
                    // 서버에서 받은 새 일정을 현재 목록에 추가
                    val newSchedule = response.body()
                    val currentList = _schedules.value.orEmpty().toMutableList()
                    newSchedule?.let {
                        currentList.add(it)
                        _schedules.value = currentList
                    }
                } else {
                    _error.value = "일정 생성에 실패했습니다."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 기존 일정을 수정합니다.
     * @param roomId 방 ID
     * @param scheduleId 수정할 일정 ID
     * @param schedule 수정할 일정 정보
     * 
     * 동작 과정:
     * 1. 로딩 상태를 true로 설정
     * 2. API 호출하여 일정 수정 시도
     * 3. 성공 시:
     *    - 현재 목록에서 해당 일정을 찾아 업데이트
     *    - schedules LiveData 업데이트
     * 4. 실패 시: error LiveData에 에러 메시지 설정
     * 5. 완료 시: 로딩 상태를 false로 설정
     */
    fun updateSchedule(roomId: Long, scheduleId: Long, schedule: Schedule) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.updateSchedule(roomId, scheduleId, schedule)
                if (response.isSuccessful) {
                    val updatedSchedule = response.body()
                    val currentList = _schedules.value.orEmpty().toMutableList()
                    val position = currentList.indexOfFirst { it.schedulesId == scheduleId }
                    if (position != -1 && updatedSchedule != null) {
                        currentList[position] = updatedSchedule
                        _schedules.value = currentList
                    }
                } else {
                    _error.value = "일정 수정에 실패했습니다."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 일정을 삭제합니다.
     * @param roomId 방 ID
     * @param scheduleId 삭제할 일정 ID
     * 
     * 동작 과정:
     * 1. 로딩 상태를 true로 설정
     * 2. API 호출하여 일정 삭제 시도
     * 3. 성공 시:
     *    - 현재 목록에서 해당 일정을 제거
     *    - schedules LiveData 업데이트
     * 4. 실패 시: error LiveData에 에러 메시지 설정
     * 5. 완료 시: 로딩 상태를 false로 설정
     */
    fun deleteSchedule(roomId: Long, scheduleId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.deleteSchedule(roomId, scheduleId)
                if (response.isSuccessful) {
                    val currentList = _schedules.value.orEmpty().toMutableList()
                    currentList.removeAll { it.schedulesId == scheduleId }
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