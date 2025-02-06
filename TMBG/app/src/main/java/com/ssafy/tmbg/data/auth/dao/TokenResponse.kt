package com.ssafy.tmbg.data.auth.dao

data class TokenResponse(
    val accessToken: String,
    val refreshToken : String,
    val tokenExpiresAt: String
)
