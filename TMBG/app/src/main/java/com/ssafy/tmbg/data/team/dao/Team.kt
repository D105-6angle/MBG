package com.ssafy.tmbg.data.team.dao

// Team 데이터
data class Team(
    val roomId: Int,
    val roomName: String,
    val inviteCode: String,
    val numOfGroups: Int,
    val groups: List<Group>
)

data class Group(
    val groupNo: Int,
    val memberCount: Int
)
