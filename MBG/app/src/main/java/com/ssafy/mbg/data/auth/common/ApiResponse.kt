package com.ssafy.mbg.data.auth.common

import com.ssafy.mbg.data.mypage.dao.ErrorDetail

/**
 * API 응답의 공통 형식을 정의하는 wrapper 클래스
 *
 * @param T 응답 데이터의 타입
 * @property status 응답 상태 코드 (에러 발생 시)
 * @property message 응답 메시지 (에러 발생 시)
 * @property error 에러 상세 정보 (에러 발생 시)
 * @property data 실제 응답 데이터 (성공 시)
 */
data class ApiResponse<T>(
    val status: Int? = null,
    val message: String? = null,
    val error: ErrorDetail? = null,
    val data: T? = null
)