package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.request.LoginRequest
import com.ssafy.mbg.data.auth.response.LoginResponse
import com.ssafy.mbg.data.auth.request.RegisterRequest
import com.ssafy.mbg.data.auth.response.RegisterResponse
import com.ssafy.mbg.data.auth.response.WithdrawResponse

interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest) : Result<LoginResponse>
    suspend fun register(registerRequest: RegisterRequest) : Result<RegisterResponse>
    suspend fun updateNickname(userId: Long, nickname : String) : Result<Unit>
    suspend fun withDraw(userId : Long) : Result<Unit>
}