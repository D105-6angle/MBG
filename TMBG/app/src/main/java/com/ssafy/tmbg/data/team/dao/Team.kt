package com.ssafy.tmbg.data.team.dao

// Team 데이터
data class Team(
    val roomId: Number,
    val roomName: String,
    val inviteCode: String,
    val numOfGroups: Number,
    val teacher: Teacher,
)

data class Teacher(
    val teacherId: Number,
    val name: String,
    val groups: List<Group>
)

data class Group(
    val groupNo: Number,
    val memberCount: Number
)
