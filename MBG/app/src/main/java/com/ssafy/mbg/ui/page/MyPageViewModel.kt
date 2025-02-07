package com.ssafy.mbg.ui.page

import androidx.lifecycle.ViewModel
import com.ssafy.mbg.data.mypage.repository.MyPageRepository
import com.ssafy.mbg.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * 마이페이지 화면의 UI 상태를 관리하는 ViewModel
 */
@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val repository : MyPageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MyPageState>(MyPageState.Initial)


    private val userId: String
        get() = userPreferences.userId ?: throw  IllegalArgumentException("유저 ID를 찾을 수 없음")
    /**
     * 사용자의 마이페이지 정보를 조회
     *
     * @param userId 조회할 사용자의 ID
     * @throws Exception 네트워크 요청 실패 시 발생
     */
    suspend fun getUserInfo(userId: String) {
        _uiState.value = MyPageState.Loading
        try {
            // response 받아오기
            val res = repository.getUserInfo(userId)
            // 성공 적으로 받아오면
            if (res.isSuccessful) {
                // 데이터가 비어 있지 않을 경우
                res.body()?.data?.let { userInfo ->
                    // State 값 변경
                    _uiState.value = MyPageState.Success(userInfo = userInfo)
                // 데이터가 비어있다면
                } ?: run {
                    // State 값 변경
                    _uiState.value = MyPageState.Error("데이터가 비어있습니다")
                }
            // 서버 에러 응답 받았을 시
            } else {
                // TODO: 서버 에러 응답 처리
                // status, message, error code에 따른 처리
                _uiState.value = MyPageState.Error(
                    // 에러 메시지가 있다면 바로 에러메시지를 보여주고, 아니라면 "알 수 없는 에러 발생 메시지"
                    message = res.body()?.message ?: "알 수 없는 에러가 발생했습니다"
                )
            }
        // 네트워크 오류 시
        } catch (e: Exception) {
            // 네트워크 연결 실패 등의 예외 상황 처리
            _uiState.value = MyPageState.Error("네트워크 오류가 발생 했습니다.")
        }
    }

    /**
     * 특정 문제 풀이 기록의 상세 정보를 조회
     *
     * @param userId 사용자 ID
     * @param logId 조회할 문제 풀이 기록의 ID
     * @throws Exception 네트워크 요청 실패 시 발생
     */
    suspend fun getDetailProblem(userId: String, logId: String) {
        _uiState.value = MyPageState.Loading
        try {
            val res = repository.getDetailProblem(userId, logId)
            if (res.isSuccessful) {
                res.body()?.data?.let {problemHistory ->
                    _uiState.value = MyPageState.Success(problemHistory = problemHistory)
                } ?: run {
                    _uiState.value = MyPageState.Error("데이터가 비어있습니다.")
                }
            } else {
                // status, message, error code에 따른 처리
                _uiState.value = MyPageState.Error(
                    message = res.body()?.message ?: "알 수 없는 에러가 발생했습니다"
                )
            }
        } catch (e: Exception) {
            // 네트워크 연결 실패 등의 예외 상황 처리
            _uiState.value = MyPageState.Error("네트워크 오류가 발생했습니다.")
        }
    }

    /**
     * @param userId 사용자 ID
     */
    suspend fun getUserProfile(userId: String) {
        _uiState.value = MyPageState.Loading
        try {
            val res = repository.getProfile(userId)
            if (res.isSuccessful) {
                // 성공 처리
                res.body()?.data?.let { userProfile ->
                    _uiState.value = MyPageState.Success(userProfile = userProfile)
                } ?: run {
                    _uiState.value = MyPageState.Error("데이터가 비어있습니다.")
                }
            } else {
                // 실패 처리
                _uiState.value = MyPageState.Error(
                    message = res.body()?.message ?: "알 수 없는 에러가 발생했습니다"
                )
            }
        } catch (e:Exception) {
            _uiState.value = MyPageState.Error("네트워크 오류가 발생했습니다.")
            // 예외 처리
        }
    }

    /**
     * @param userId 사용자 ID
     * @request newNickname 변경할 닉네임
     */

    suspend fun updateNickname(userId: String, newNickname: String) {
        _uiState.value = MyPageState.Loading
        try {
            val res = repository.updateNickname(userId, newNickname)
            if (res.isSuccessful) {
                // 성공 처리
                _uiState.value = MyPageState.Success(message = "닉네임이 변경 되었어요!")
            } else {
                // 에러 처리
                _uiState.value = MyPageState.Error(
                    message = res.body()?.message ?: "알 수 없는 에러가 발생했습니다"
                )
            }
        } catch (e: Exception) {
            // 네트워크 예외 처리
            _uiState.value = MyPageState.Error("네트워크 오류가 발생 했습니다.")
        }
    }

    /**
     * @param userId 사용자 ID
     */
    suspend fun deleteUser(userId: String) {
        _uiState.value = MyPageState.Loading
        try {
            val res = repository.deleteUser(userId)
            if (res.isSuccessful) {
                // 성공 처리
                userPreferences.userId = null
                _uiState.value = MyPageState.Success()
            } else {
                // 예외 처리
                _uiState.value = MyPageState.Error(
                    message = res.body()?.message ?: "알 수 없는 에러가 발생 했습니다."
                )
            }
        } catch (e: Exception) {
            // 네트워크 예외 처리
            _uiState.value = MyPageState.Error("네트워크 오류가 발생했습니다.")
        }
    }
}