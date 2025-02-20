package com.ssafy.mbg.data.mypage.dao

/**
 * 닉네임 변경 요청을 위한 DTO 클래스
 *
 * @property nickname 변경하고자 하는 새로운 닉네임
 */
data class UpdateNicknameRequest(
    val nickname: String
)