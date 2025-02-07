package com.ssafy.mbg.data.auth.repository

import com.google.gson.Gson
import com.ssafy.mbg.api.AuthApi
import com.ssafy.mbg.data.auth.common.ApiResponse
import com.ssafy.mbg.data.auth.dto.LoginErrorResponse
import com.ssafy.mbg.data.auth.dto.LoginRequest
import com.ssafy.mbg.data.auth.dto.LoginResponse
import com.ssafy.mbg.data.auth.dto.RegisterRequest
import com.ssafy.mbg.data.auth.dto.RegisterResponse
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    companion object {
        // HTTP 상태 코드 정의
        private const val STATUS_OK = 200          // 정상 응답
        private const val STATUS_NO_CONTENT = 204  // 회원가입 필요
        private const val STATUS_BAD_REQUEST = 400 // 잘못된 요청
        private const val STATUS_UNAUTHORIZED = 401 // 인증 실패

        // 에러 메시지 정의
        private const val ERROR_LOGIN_NO_DATA = "회원가입이 필요합니다."
        private const val ERROR_REGISTER_NO_DATA = "회원가입 데이터가 없습니다."
        private const val ERROR_ALREADY_REGISTERED = "이미 가입된 회원입니다."
        private const val ERROR_UNAUTHORIZED = "인증에 실패했습니다."
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
                    STATUS_NO_CONTENT -> ERROR_LOGIN_NO_DATA
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
                    STATUS_BAD_REQUEST -> ERROR_ALREADY_REGISTERED
                    STATUS_UNAUTHORIZED -> ERROR_UNAUTHORIZED
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
            when (response.code()) {
                STATUS_OK -> {
                    val apiResponse = response.body()
                    apiResponse?.data?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception(apiResponse?.error?.toString() ?: noDataError))
                }
                STATUS_NO_CONTENT -> {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        val errorResponse = Gson().fromJson(errorBody, LoginErrorResponse::class.java)
                        Result.failure(Exception(errorResponse?.message ?: ERROR_LOGIN_NO_DATA))
                    } else {
                        Result.failure(Exception(ERROR_LOGIN_NO_DATA))
                    }
                }
                else -> {
                    val errorMessage = handleErrorCode(response.code()) ?: defaultError
                    Result.failure(Exception(errorMessage))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}