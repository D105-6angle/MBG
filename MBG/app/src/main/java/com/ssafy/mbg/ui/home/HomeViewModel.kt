package com.ssafy.mbg.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.mbg.data.home.dao.JoinRequest
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

    init {
        _roomId.value = 0L
        _location.value = ""
        _error.value = ""
        _numOfGroups.value = 0L
    }


    fun joinRoom(joinRequest: JoinRequest) {
        viewModelScope.launch {
            try {
                val response = repository.joinRoom(joinRequest)

                if (response.isSuccessful) {
                    response.body()?.let { joinResponse ->
                        _roomId.value = joinResponse.roomId
                        _location.value = joinResponse.location
                        _numOfGroups.value = joinResponse.numOfGroup
                        Log.d("HomeViewModel", "방 입장 성공 - roomId: ${joinResponse.roomId}, location: ${joinResponse.location}, numOfGroups: ${joinResponse.numOfGroup}")
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


}