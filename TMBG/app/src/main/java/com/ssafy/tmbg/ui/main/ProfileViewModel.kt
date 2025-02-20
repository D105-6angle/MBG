package com.ssafy.tmbg.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ssafy.tmbg.data.profile.repository.ProfileRepository
import com.ssafy.tmbg.data.profile.repository.ProfileRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "ProfileViewModle"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository : ProfileRepositoryImpl
) : ViewModel(){
    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Initial)
    val uiState = _uiState.asStateFlow()

    suspend fun getProfile(userId: Long, roomId: Long? = null) {
        Log.d(TAG, "getProfile 호출 - userId: $userId, roomId: $roomId")
        _uiState.value = ProfileState.Loading
        try {
            Log.d(TAG, "API 호출 시작")
            val response = repository.getProgile(userId, roomId)
            Log.d(TAG, "API 응답 수신 - isSuccessful: ${response.isSuccessful}")
            Log.d(TAG, "응답 코드: ${response.code()}")
            Log.d(TAG, "응답 메시지: ${response.message()}")
            Log.d(TAG, "응답 바디: ${response.body()}")

            if (response.isSuccessful) {
                response.body()?.let { profileResponse ->
                    Log.d(TAG, "프로필 데이터 수신 성공: $profileResponse")
                    _uiState.value = ProfileState.Success(profileResponse = profileResponse)
                } ?: run {
                    Log.e(TAG, "응답 바디가 null임")
                    _uiState.value = ProfileState.Error("데이터가 비어있습니다")
                }
            } else {
                Log.e(TAG, "서버 오류 - 코드: ${response.code()}, 메시지: ${response.errorBody()?.string()}")
                _uiState.value = ProfileState.Error("서버 오류가 발생했습니다")
            }
        } catch (e: Exception) {
            Log.e(TAG, "네트워크 오류 발생", e)
            Log.e(TAG, "상세 에러: ${e.message}")
            e.printStackTrace()
            _uiState.value = ProfileState.Error("네트워크 오류가 발생했습니다")
        }
    }
}