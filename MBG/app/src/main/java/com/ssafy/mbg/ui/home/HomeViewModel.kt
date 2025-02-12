package com.ssafy.mbg.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.mbg.data.home.dao.JoinGroupRequest
import com.ssafy.mbg.data.home.dao.JoinGroupResponse
import com.ssafy.mbg.data.home.dao.JoinRoomRequest
import com.ssafy.mbg.data.home.dao.JoinRoomResponse
import com.ssafy.mbg.data.home.repository.HomeRepository
import com.ssafy.mbg.di.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _roomId = MutableLiveData<Long>()
    val roomId: LiveData<Long> = _roomId

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    private val _numOfGroups = MutableLiveData<Long>()
    val numOfGroups: LiveData<Long> = _numOfGroups

    private val _joinGroupResult = MutableLiveData<JoinGroupResponse>()
    val joinGroupResult: LiveData<JoinGroupResponse> = _joinGroupResult

    private val _isJoinedGroup = MutableLiveData<Boolean>()
    val isJoinedGroup: LiveData<Boolean> = _isJoinedGroup

    private var isClearing = false  // clearGroup 작업 중인지 추적하는 플래그 추가

    init {
        if (!isClearing) {  // clearGroup 작업 중이 아닐 때만 초기화
            _roomId.value = userPreferences.roomId ?: 0L
            _location.value = userPreferences.location
            _error.value = ""
            _numOfGroups.value = 0L
            
            // roomId가 null이면 무조건 false
            _isJoinedGroup.value = userPreferences.roomId != null && userPreferences.groupNo > 0
            
            Log.d("HomeViewModel", "Initialized with groupNo: ${userPreferences.groupNo}, roomId: ${userPreferences.roomId}, isJoinedGroup: ${_isJoinedGroup.value}")
        }
    }

    fun joinRoom(joinRoomRequest: JoinRoomRequest) {
        viewModelScope.launch {
            try {
                val response = repository.joinRoom(joinRoomRequest)

                if (response.isSuccessful) {
                    Log.d("HomeViewModel", "API Response Body: ${response.body()}")
                    response.body()?.let { joinResponse ->
                        _roomId.value = joinResponse.roomId
                        _location.value = joinResponse.location
                        _numOfGroups.value = joinResponse.numOfGroups
                        
                        Log.d("HomeViewModel", "방 입장 성공 - roomId: ${joinResponse.roomId}, location: ${joinResponse.location}, numOfGroups: ${joinResponse.numOfGroups}")
                        Log.d("HomeViewModel", "저장된 값 - roomId: ${userPreferences.roomId}, location: ${userPreferences.location}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("HomeViewModel", "초대코드로 입장 실패 ${response.code()}, Error: $errorBody")
                    _error.value = "입장에 실패했습니다. (${response.code()})"
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "초대코드 입장 에러", e)
                _error.value = "네트워크 오류: ${e.message}"
            }
        }
    }

    fun joinGroup(roomId: Long, groupNo: Int) {
        viewModelScope.launch {
            try {
                val response = repository.joinGroup(roomId, JoinGroupRequest(groupNo))
                if (response.isSuccessful) {
                    response.body()?.let { joinGroupResponse ->
                        _joinGroupResult.value = joinGroupResponse
                        // 그룹 참여 성공 시 상태 업데이트
                        _isJoinedGroup.value = true
                    }
                } else {
                    _error.value = "그룹 참여에 실패했습니다. (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "네트워크 오류: ${e.message}"
            }
        }
    }

    fun clearGroup() {
        viewModelScope.launch {
            isClearing = true  // clearGroup 작업 시작
            Log.d("HomeViewModel", "Clearing group data, before - groupNo: ${userPreferences.groupNo}, roomId: ${userPreferences.roomId}")
            
            // UserPreferences 초기화
            userPreferences.apply {
                roomId = null
                location = ""
                groupNo = 0
                codeId = ""
            }
            
            // LiveData 값들 초기화
            _isJoinedGroup.postValue(false)  // postValue 사용
            _roomId.postValue(0L)
            _location.postValue("")
            _numOfGroups.postValue(0L)
            _error.postValue("")
            
            // 상태가 제대로 업데이트되었는지 확인
            Log.d("HomeViewModel", "Group data cleared - isJoinedGroup: ${_isJoinedGroup.value}, groupNo: ${userPreferences.groupNo}, roomId: ${userPreferences.roomId}")
            isClearing = false  // clearGroup 작업 완료
        }
    }

    fun updateLocation(location: String) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Updating location - Previous: ${userPreferences.location}, New: $location")
            userPreferences.location = location
            _location.value = location
            Log.d("HomeViewModel", "Location updated in UserPreferences: ${userPreferences.location}")
        }
    }

    fun deleteMember(roomId: Int, groupNo: Int) {
        viewModelScope.launch {
            try {
                userPreferences.userId?.let { userId ->
                    Log.d("HomeViewModel", "Attempting to delete member - userId: $userId, roomId: $roomId, groupNo: $groupNo")
                    val response = repository.deleteMember(roomId, groupNo, userId)
                    Log.d("HomeViewModel", "Delete member API response: ${response.code()}")
                    
                    if (response.isSuccessful) {
                        clearGroup()
                        _isJoinedGroup.value = false
                        Log.d("HomeViewModel", "Member deleted successfully - userId: $userId, roomId: $roomId, groupNo: $groupNo")
                    } else {
                        _error.value = "멤버 삭제에 실패했습니다. (${response.code()})"
                        Log.e("HomeViewModel", "Failed to delete member: ${response.code()}")
                    }
                } ?: run {
                    _error.value = "사용자 정보를 찾을 수 없습니다."
                    Log.e("HomeViewModel", "UserId is null in UserPreferences")
                }
            } catch (e: Exception) {
                _error.value = "네트워크 오류: ${e.message}"
                Log.e("HomeViewModel", "Error deleting member", e)
            }
        }
    }

}