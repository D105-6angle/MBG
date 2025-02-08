package com.ssafy.mbg.data.auth.dto

data class RegisterResponse(
    val userId : Long,
    val nickname : String,
    val accessToken : String,
    val refreshToken : String
)
