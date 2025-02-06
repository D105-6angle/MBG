package com.ssafy.mbg.data.auth.dto

data class LoginResponse(
    val userId : String,
    val codeId : String,
    val name : String,
    val nickname : String,
    val email : String,
    val isDeleted : Boolean,
    val createdAt : String
)
