package com.ssafy.tmbg.data.auth.response

data class RegisterResponse(
    val userId : Long,
    val nickname : String,
    val accessToken : String,
    val refreshToken : String
)