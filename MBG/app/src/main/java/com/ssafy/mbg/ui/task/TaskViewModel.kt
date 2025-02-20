package com.ssafy.mbg.ui.task

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.mbg.data.task.dao.Member
import com.ssafy.mbg.data.task.dao.Schedule
import com.ssafy.mbg.data.task.repository.TaskRepositoryImpl
import com.ssafy.mbg.di.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepositoryImpl,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _schedules = MutableLiveData<List<Schedule>>()
    val schedules: LiveData<List<Schedule>> = _schedules

    private val _teamMembers = MutableLiveData<List<Member>>()
    val teamMembers: LiveData<List<Member>> = _teamMembers

    private val _state = MutableLiveData<ScheduleState>(ScheduleState.Initial)
    val state: LiveData<ScheduleState> = _state

    fun getSchedules() {
        val roomId = userPreferences.roomId
        val groupNo = userPreferences.groupNo.toLong()

        if (roomId == null) {
            _state.value = ScheduleState.Error("방을 먼저 생성 해주세요!")
            return
        }

        viewModelScope.launch {
            try {
                _state.value = ScheduleState.Loading

                // 일정 조회
                val schedulesResult = repository.getSchedules(roomId)

                // 팀 멤버 조회
                val teamResult = repository.getMyTeams(roomId, groupNo)

                schedulesResult.onSuccess { scheduleResponse ->
                    teamResult.onSuccess { teamResponse ->
                        // 일정 없어도 팀 멤버는 보여주도록 수정
                        _teamMembers.value = teamResponse.members

                        if (scheduleResponse.schedules.isNotEmpty()) {
                            _schedules.value = scheduleResponse.schedules
                            _state.value = ScheduleState.Success(scheduleResponse.schedules)
                        } else {
                            _state.value = ScheduleState.Error("일정이 없습니다.")
                            _schedules.value = emptyList()
                        }
                    }.onFailure {
                        _state.value = ScheduleState.Error(it.message ?: "팀 정보를 불러오는 데 실패했습니다.")
                    }
                }.onFailure {
                    _state.value = ScheduleState.Error(it.message ?: "일정을 불러오는 데 실패했습니다.")
                    _schedules.value = emptyList()
                }
            } catch (e: Exception) {
                _state.value = ScheduleState.Error(e.message ?: "네트워크 오류가 발생했습니다.")
                _schedules.value = emptyList()
            }
        }
    }
}