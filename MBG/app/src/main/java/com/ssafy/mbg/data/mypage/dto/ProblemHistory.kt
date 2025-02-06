package com.ssafy.mbg.data.mypage.dto

/**
 * 사용자가 푼 문제의 기록을 나타내는 클래스
 *
 * @property logId 문제 풀이 기록의 고유 ID
 * @property cardId 문화재 카드의 고유 ID
 * @property name 문화재 이름
 * @property imageUrl 문화재 이미지 리소스 ID (null 가능)
 * @property lastSolvedAt 마지막 풀이 날짜
 * @property result 문제 풀이 결과 (성공: true, 실패: false)
 * @property description 문화재에 대한 설명 (null 가능)
 */
data class ProblemHistory(
    val logId: String,
    val cardId: String,
    val name: String,
    val imageUrl: Int,
    val lastSolvedAt: String,
    val result: Boolean,
    val description: String? = null
)