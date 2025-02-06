package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.dto.LoginRequest
import com.ssafy.mbg.data.auth.dto.LoginResponse
import com.ssafy.mbg.data.auth.dto.RegisterRequest
import com.ssafy.mbg.data.auth.dto.RegisterResponse

interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest) : Result<LoginResponse>
    suspend fun register(registerRequest: RegisterRequest) : Result<RegisterResponse>
}