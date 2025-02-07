package com.ssafy.mbg.data.auth.dto

data class LoginResponse(
    val userId : String,
    val accessToken : String,
    val refreshToken : String,
    val nickname : String,
    val status : String? = null
)
