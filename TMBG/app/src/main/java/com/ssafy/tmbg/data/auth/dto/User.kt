package com.ssafy.tmbg.data.auth.dto

data class User(
    val userId : String,
    val providerId : String,
    val email : String,
    val name : String,
    val nickname : String,
    val accessToken : String,
    val refreshToken : String,
    val tokenExpiresAt : String
)
