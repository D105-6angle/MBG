package com.ssafy.mbg.ui.page

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ssafy.mbg.data.mypage.repository.MyPageRepositoryImpl
import com.ssafy.mbg.di.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "MyPageViewModel"

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val repository: MyPageRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyPageState>(MyPageState.Initial)
    val uiState = _uiState.asStateFlow()

    suspend fun getProfile(userId: Long, roomId: Long? = null) {
        Log.d(TAG, "getProfile 호출 - userId: $userId, roomId: $roomId")
        _uiState.value = MyPageState.Loading
        try {
            Log.d(TAG, "API 호출 시작")
            val response = repository.getProfile(userId, roomId)
            Log.d(TAG, "API 응답 수신 - isSuccessful: ${response.isSuccessful}")
            Log.d(TAG, "응답 코드: ${response.code()}")
            Log.d(TAG, "응답 메시지: ${response.message()}")
            Log.d(TAG, "응답 바디: ${response.body()}")

            if (response.isSuccessful) {
                response.body()?.let { profileResponse ->
                    Log.d(TAG, "프로필 데이터 수신 성공: $profileResponse")
                    _uiState.value = MyPageState.Success(profileResponse = profileResponse)
                } ?: run {
                    Log.e(TAG, "응답 바디가 null임")
                    _uiState.value = MyPageState.Error("데이터가 비어있습니다")
                }
            } else {
                Log.e(TAG, "서버 오류 - 코드: ${response.code()}, 메시지: ${response.errorBody()?.string()}")
                _uiState.value = MyPageState.Error("서버 오류가 발생했습니다")
            }
        } catch (e: Exception) {
            Log.e(TAG, "네트워크 오류 발생", e)
            Log.e(TAG, "상세 에러: ${e.message}")
            e.printStackTrace()
            _uiState.value = MyPageState.Error("네트워크 오류가 발생했습니다")
        }
    }

    suspend fun getDetailProblem(userId: Long, cardId: Long) {
        Log.d(TAG, "getDetailProblem 호출 - userId: $userId, cardId: $cardId")
        _uiState.value = MyPageState.Loading
        try {
            Log.d(TAG, "상세 정보 API 호출 시작")
            val response = repository.getDetailProblem(userId, cardId)
            Log.d(TAG, "상세 정보 응답 수신 - isSuccessful: ${response.isSuccessful}")
            Log.d(TAG, "응답 코드: ${response.code()}")
            Log.d(TAG, "응답 메시지: ${response.message()}")
            Log.d(TAG, "응답 바디: ${response.body()}")

            if (response.isSuccessful) {
                response.body()?.let { problemResponse ->
                    Log.d(TAG, "상세 정보 데이터 수신 성공: $problemResponse")
                    _uiState.value = MyPageState.Success(problemResponse = problemResponse)
                } ?: run {
                    Log.e(TAG, "상세 정보 응답 바디가 null임")
                    _uiState.value = MyPageState.Error("데이터가 비어있습니다")
                }
            } else {
                Log.e(TAG, "서버 오류 - 코드: ${response.code()}, 메시지: ${response.errorBody()?.string()}")
                _uiState.value = MyPageState.Error("서버 오류가 발생했습니다")
            }
        } catch (e: Exception) {
            Log.e(TAG, "상세 정보 조회 중 네트워크 오류 발생", e)
            Log.e(TAG, "상세 에러: ${e.message}")
            e.printStackTrace()
            _uiState.value = MyPageState.Error("네트워크 오류가 발생했습니다")
        }
    }

    fun resetState() {
        Log.d(TAG, "상태 초기화")
        _uiState.value = MyPageState.Initial
    }
}