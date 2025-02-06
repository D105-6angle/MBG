package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.dto.LoginRequest
import com.ssafy.mbg.data.auth.dto.LoginResponse
import com.ssafy.mbg.data.auth.dto.RegisterRequest
import com.ssafy.mbg.data.auth.dto.RegisterResponse
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {
    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = authApi.login(loginRequest)
            if (response.isSuccessful) {
                // 성공시에는 data가 바로 내려옴
                response.body()?.data?.let { userInfo ->
                    Result.success(userInfo)
                } ?: Result.failure(Exception("로그인 데이터가 없습니다."))
            } else {
                // 실패시에는 status code와 에러 메시지가 내려옴
                val errorBody = response.errorBody()?.string()
                when (response.code()) {
                    400 -> Result.failure(Exception("잘못된 요청입니다."))
                    401 -> Result.failure(Exception("인증이 필요합니다."))
                    else -> Result.failure(Exception(errorBody ?: "로그인 실패"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(registerRequest: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = authApi.register(registerRequest)
            if (response.isSuccessful) {
                // 성공시에는 data가 바로 내려옴
                response.body()?.data?.let { registerInfo ->
                    Result.success(registerInfo)
                } ?: Result.failure(Exception("회원가입 데이터가 없습니다."))
            } else {
                // 실패시에는 status code와 에러 메시지가 내려옴
                val errorBody = response.errorBody()?.string()
                when (response.code()) {
                    400 -> Result.failure(Exception("이미 가입된 회원입니다."))
                    else -> Result.failure(Exception(errorBody ?: "회원가입 실패"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}