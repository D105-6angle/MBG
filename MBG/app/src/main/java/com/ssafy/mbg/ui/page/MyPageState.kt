package com.ssafy.mbg.ui.page

import com.ssafy.mbg.data.auth.dto.UserProfile
import com.ssafy.mbg.data.mypage.dto.ProblemHistory
import com.ssafy.mbg.data.mypage.dto.UserInfo

/**
 * 마이페이지의 UI 상태를 나타내는 sealed interface
 */
sealed interface MyPageState {
    /**
     * 초기 상태
     * 데이터 로딩이 시작되기 전의 상태
     */
    data object Initial : MyPageState

    /**
     * 로딩 상태
     * 데이터를 불러오는 중인 상태
     */
    data object Loading : MyPageState

    /**
     * 성공 상태
     * 데이터 로딩이 성공적으로 완료된 상태
     *
     * @property userInfo 사용자 정보
     * @property problemHistory 문제 풀이 기록
     * @property userProfile 사용자 프로필 정보
     * @property message 성공 메시지 (닉네임 변경 등의 작업 완료 시)
     */
    data class Success(
        val userInfo: UserInfo? = null,
        val problemHistory: ProblemHistory? = null,
        val userProfile: UserProfile? = null,
        val message: String? = null
    ) : MyPageState

    /**
     * 에러 상태
     * 데이터 로딩 실패 또는 작업 실패 상태
     *
     * @property message 에러 메시지
     */
    data class Error(
        val message: String
    ) : MyPageState
}