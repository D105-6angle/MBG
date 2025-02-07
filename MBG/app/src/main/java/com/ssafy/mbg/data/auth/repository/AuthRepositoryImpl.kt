package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.common.ApiResponse
import com.ssafy.mbg.data.auth.dto.LoginRequest
import com.ssafy.mbg.data.auth.dto.LoginResponse
import com.ssafy.mbg.data.auth.dto.RegisterRequest
import com.ssafy.mbg.data.auth.dto.RegisterResponse
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    companion object {
        private const val ERROR_LOGIN_NO_DATA = "로그인 데이터가 없습니다."
        private const val ERROR_REGISTER_NO_DATA = "회원가입 데이터가 없습니다."
        private const val ERROR_BAD_REQUEST = "잘못된 요청입니다."
        private const val ERROR_UNAUTHORIZED = "인증이 필요합니다."
        private const val ERROR_ALREADY_REGISTERED = "이미 가입된 회원입니다."
        private const val ERROR_LOGIN_FAILED = "로그인 실패"
        private const val ERROR_REGISTER_FAILED = "회원가입 실패"
    }

    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return handleApiCall(
            apiCall = { authApi.login(loginRequest) },
            noDataError = ERROR_LOGIN_NO_DATA,
            defaultError = ERROR_LOGIN_FAILED,
            handleErrorCode = { code ->
                when (code) {
                    400 -> ERROR_BAD_REQUEST
                    401 -> ERROR_UNAUTHORIZED
                    else -> null
                }
            }
        )
    }

    override suspend fun register(registerRequest: RegisterRequest): Result<RegisterResponse> {
        return handleApiCall(
            apiCall = { authApi.register(registerRequest) },
            noDataError = ERROR_REGISTER_NO_DATA,
            defaultError = ERROR_REGISTER_FAILED,
            handleErrorCode = { code ->
                when (code) {
                    400 -> ERROR_ALREADY_REGISTERED
                    else -> null
                }
            }
        )
    }

    private suspend fun <T> handleApiCall(
        apiCall: suspend () -> retrofit2.Response<ApiResponse<T>>,
        noDataError: String,
        defaultError: String,
        handleErrorCode: (Int) -> String?
    ): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                apiResponse?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception(apiResponse?.error?.toString() ?: noDataError))
            } else {
                val errorMessage = handleErrorCode(response.code()) ?: defaultError
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}