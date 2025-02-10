package com.ssafy.tmbg.data.team.dao

import java.util.*

data class TeamRequest(
    val roomName: String,
    val location : String,
    val numOfGroups:Int,
)