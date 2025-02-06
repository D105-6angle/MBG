package com.ssafy.tmbg.data.auth.dto

data class LoginResponse(
    val userId : String,
    val codeId : String,
    val accessToken : String,
    val refreshToken : String,
    val tokenExpiresAt : String
)
