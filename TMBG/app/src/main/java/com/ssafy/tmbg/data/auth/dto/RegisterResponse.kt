package com.ssafy.tmbg.data.auth.dto

data class RegisterResponse(
    val userId: String,
    val codeId : String,
    val email : String,
    val name : String,
    val nickname : String,
    val createdAt : String
)
