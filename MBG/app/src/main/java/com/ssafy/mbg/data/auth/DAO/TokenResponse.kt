package com.ssafy.mbg.data.auth.DAO

data class TokenResponse(
    val accessToken : String,
    val refreshToken : String,
    val tokenExpiresAt : String
)