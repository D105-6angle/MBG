package com.ssafy.mbg.ui.page

import androidx.lifecycle.ViewModel
import com.ssafy.mbg.data.mypage.repository.MyPageRepository
import com.ssafy.mbg.data.mypage.repository.MyPageRepositoryImpl
import com.ssafy.mbg.di.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 마이페이지 화면의 UI 상태를 관리하는 ViewModel
 *
 * @property userPreferences 사용자 설정 정보를 관리하는 클래스
 * @property repository 마이페이지 관련 데이터 처리를 담당하는 Repository
 */
@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val repository: MyPageRepositoryImpl
) : ViewModel() {

    /**
     * UI 상태를 저장하는 private MutableStateFlow
     * Initial 상태로 초기화됨
     */
    private val _uiState = MutableStateFlow<MyPageState>(MyPageState.Initial)

    /**
     * UI 레이어에서 관찰할 수 있는 읽기 전용 StateFlow
     */
    val uiState = _uiState.asStateFlow()

    /**
     * 사용자의 마이페이지 정보를 조회하는 함수
     *
     * @param userId 조회할 사용자의 ID
     *
     * Flow:
     * 1. Loading 상태로 변경
     * 2. Repository를 통해 사용자 정보 요청
     * 3. 성공/실패에 따라 적절한 상태로 변경
     *
     * @throws Exception 네트워크 요청 실패 시 발생 가능
     */
    suspend fun getUserInfo(userId: Long) {
        _uiState.value = MyPageState.Loading
        try {
            val response = repository.getUserInfo(userId)
            if (response.isSuccessful) {
                response.body()?.let { userResponse ->
                    _uiState.value = MyPageState.Success(userResponse = userResponse)
                } ?: run {
                    _uiState.value = MyPageState.Error("데이터가 비어있습니다")
                }
            } else {
                _uiState.value = MyPageState.Error(
                    message = "서버 오류가 발생했습니다"
                )
            }
        } catch (e: Exception) {
            _uiState.value = MyPageState.Error("네트워크 오류가 발생했습니다")
        }
    }

    /**
     * 특정 문제 풀이 기록의 상세 정보를 조회하는 함수
     *
     * @param userId 사용자 ID
     * @param logId 조회할 문제 풀이 기록의 ID
     *
     * Flow:
     * 1. Loading 상태로 변경
     * 2. Repository를 통해 문제 상세 정보 요청
     * 3. 성공/실패에 따라 적절한 상태로 변경
     *
     * @throws Exception 네트워크 요청 실패 시 발생 가능
     */
    suspend fun getDetailProblem(userId: Long, logId: String) {
        _uiState.value = MyPageState.Loading
        try {
            val response = repository.getDetailProblem(userId, logId)
            if (response.isSuccessful) {
                response.body()?.let { problemResponse ->
                    _uiState.value = MyPageState.Success(problemResponse = problemResponse)
                } ?: run {
                    _uiState.value = MyPageState.Error("데이터가 비어있습니다")
                }
            } else {
                _uiState.value = MyPageState.Error(
                    message = "서버 오류가 발생했습니다"
                )
            }
        } catch (e: Exception) {
            _uiState.value = MyPageState.Error("네트워크 오류가 발생했습니다")
        }
    }

    /**
     * ViewModel의 상태를 초기 상태로 리셋하는 함수
     *
     * 사용 사례:
     * - 화면 전환 시 상태 초기화
     * - 에러 상태에서 복구
     * - 새로운 데이터 로딩 전 이전 상태 클리어
     */
    fun resetState() {
        _uiState.value = MyPageState.Initial
    }
}