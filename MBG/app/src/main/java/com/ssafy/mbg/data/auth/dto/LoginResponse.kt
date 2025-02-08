package com.ssafy.mbg.data.auth.dto

data class LoginResponse(
    val userId : Long,
    val accessToken : String,
    val refreshToken : String,
    val nickname : String,
)
