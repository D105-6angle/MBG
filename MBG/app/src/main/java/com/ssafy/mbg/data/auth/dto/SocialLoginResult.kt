package com.ssafy.mbg.data.auth.dto

data class SocialLoginResult(
    val socialUserInfo: SocialUserInfo,
    val token : TokenResponse
)
