package com.ssafy.mbg.data.mypage.dao

/**
 * API 에러 상세 정보를 담는 클래스
 *
 * @property code 에러 코드 (예: "UNAUTHORIZED", "USER_NOT_FOUND" 등)
 */
data class ErrorDetail(
    val code: String
)