package com.ssafy.mbg.data.home.dao

data class JoinGroupResponse (
    val userId: Long,
    val roomId: Long,
    val groupNo: Int,
    val codeId: String,
)