package com.ssafy.tmbg.data.team.dao

// Team 데이터
data class Team(
    val roomId: Number,
    val roomName: String,
    val inviteCode: String,
    val numOfGroups: Number,
    val groups: List<Group>
)

data class Group(
    val groupNo: Number,
    val memberCount: Number
)
