package com.ssafy.tmbg.data.auth.DAO

data class TokenResponse(
    val accessToken: String,
    val refreshToken : String,
    val tokenExpiresAt: String
)
