package com.ssafy.tmbg.data.auth.dto

data class RegisterRequest(
    val providerId : String,
    val email : String,
    val name : String,
    val nickname: String
)
