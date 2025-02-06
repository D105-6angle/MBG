package com.ssafy.tmbg.data.auth.dto

data class LoginRequest(
    val providerId : String,
    val accessToken : String,
    val refreshToken : String,
    val tokenExpiresAt : String
)
