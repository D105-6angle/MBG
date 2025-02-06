package com.ssafy.mbg.data.auth.dto

data class RegisterResponse(
    val userId : Int,
    val codeId : String,
    val email : String,
    val name : String,
    val nickname : String,
    val createdAt : String
)
