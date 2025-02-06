package com.ssafy.mbg.data.auth.dto

data class TokenResponse(
    val accessToken : String,
    val refreshToken : String,
    val tokenExpiresAt : String
)