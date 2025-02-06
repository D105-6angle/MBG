package com.ssafy.mbg.data.auth.dto

data class RegisterRequest(
    val providerId : String,
    val email : String,
    val name : String,
    val nickname : String
)
