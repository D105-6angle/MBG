package com.ssafy.tmbg.data.auth.dto

data class SocialToken(
    val accessToken: String,
    val refreshToken : String,
    val tokenExpiresAt: String
)
