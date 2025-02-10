package com.ssafy.tmbg.data.auth.response

data class LoginResponse(
    val userId : Long,
    val accessToken : String,
    val refreshToken : String,
    val nickname : String
)
