package com.ssafy.mbg.data.mypage.dto

/**
 * 사용자의 기본 정보와 문제 풀이 기록을 포함하는 클래스
 *
 * @property userId 사용자의 고유 ID
 * @property nickname 사용자의 닉네임
 * @property solvedProblems 사용자가 푼 문제들의 기록 리스트 (null 가능)
 */
data class UserInfo(
    val userId: String,
    val nickname: String,
    val solvedProblems: List<ProblemHistory>? = null
)