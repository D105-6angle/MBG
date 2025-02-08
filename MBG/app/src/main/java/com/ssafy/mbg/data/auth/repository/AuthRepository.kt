package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.dto.LoginRequest
import com.ssafy.mbg.data.auth.dto.LoginResponse
import com.ssafy.mbg.data.auth.dto.RegisterRequest
import com.ssafy.mbg.data.auth.dto.RegisterResponse
import com.ssafy.mbg.data.auth.dto.UpdateUserRequest
import com.ssafy.mbg.data.auth.dto.UpdateUserResponse
import com.ssafy.mbg.data.auth.dto.WithdrawResponse

interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest) : Result<LoginResponse>
    suspend fun register(registerRequest: RegisterRequest) : Result<RegisterResponse>
    suspend fun updateNickname(userId: Long, nickname : String) : Result<UpdateUserResponse>
    suspend fun withDraw(userId : Long) : Result<WithdrawResponse>
}