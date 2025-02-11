package com.ssafy.tmbg.data.team.dao

data class TeamCreateResponse(
    val roomId: Int,
    val location: String,
    val roomName: String,
    val inviteCode: String,
    val numOfGroups: Int,
    val createdAt: String,
)