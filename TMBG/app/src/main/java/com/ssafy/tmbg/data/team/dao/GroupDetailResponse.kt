package com.ssafy.tmbg.data.team.dao

// Team 데이터
data class GroupDetailResponse(
    val groupNo: Int,
    val progress: Long,               // (완료 미션 수 / 전체 미션 수)
    val members: List<MemberDto>,        // 조에 속한 멤버 목록
    val verificationPhotos: List<VerificationPhotos>, // 조장이 올린 인증샷
    val visitedPlaces: List<VisitedPlace>,
)

data class MemberDto(
    val userId: Long,
    val nickname: String,
    val codeId: String,
)

data class VerificationPhotos(
    val pictureId: Long,
    val pictureUrl: String,
    val missionId: Long,
    val completionTime: String
)

data class VisitedPlace(
    val missionId: Long,
    val positionName: String,
    val completedAt: String
)