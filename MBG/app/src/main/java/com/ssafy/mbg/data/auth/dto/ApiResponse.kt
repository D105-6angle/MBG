package com.ssafy.mbg.data.auth.dto

data class ApiResponse<T>(
    val status: Int,
    val message : String,
    val data: T? = null,
    val error: String? = null
)