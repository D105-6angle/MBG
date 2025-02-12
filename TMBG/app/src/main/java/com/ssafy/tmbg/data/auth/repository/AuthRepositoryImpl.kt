package com.ssafy.tmbg.data.auth.repository

import android.util.Log
import com.google.gson.Gson
import com.ssafy.tmbg.api.AuthApi
import com.ssafy.tmbg.data.auth.request.LoginRequest
import com.ssafy.tmbg.data.auth.request.RegisterRequest
import com.ssafy.tmbg.data.auth.request.UpdateUserRequest
import com.ssafy.tmbg.data.auth.response.ErrorResponse
import com.ssafy.tmbg.data.auth.response.LoginResponse
import com.ssafy.tmbg.data.auth.response.RegisterResponse
import com.ssafy.tmbg.data.auth.response.WithdrawResponse
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi : AuthApi,
) : AuthRepository {

    companion object {
        // HTTP 상태 코드 정의
        private const val STATUS_OK = 200          // 정상 응답
        private const val STATUS_NO_CONTENT = 204  // 회원가입 필요
        private const val STATUS_BAD_REQUEST = 400 // 잘못된 요청
        private const val STATUS_UNAUTHORIZED = 401 // 인증 실패
        private const val STATUS_FORBIDDEN = 403

        // 에러 메시지 정의
        private const val ERROR_LOGIN_NO_DATA = "회원가입이 필요합니다."
        private const val ERROR_UNAUTHORIZED = "필수 정보가 누락되었습니다."
        private const val ERROR_REGISTER_NO_DATA = "회원가입 데이터가 없습니다."
        private const val ERROR_ALREADY_REGISTERED = "이미 가입된 회원입니다."
        private const val ERROR_LOGIN_FAILED = "로그인 실패"
        private const val ERROR_REGISTER_FAILED = "회원가입 실패"
    }

    /**
     * 로그인 요청을 처리하는 함수
     * @param loginRequest 로그인에 필요한 데이터를 담은 요청 객체
     * @return Result<LoginResponse> 로그인 성공 시 토큰 정보를 담은 응답, 실패 시 에러 메시지
     */
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

    /**
     * 회원가입 요청을 처리하는 함수
     * @param registerRequest 회원가입에 필요한 데이터를 담은 요청 객체
     * @return Result<RegisterResponse> 회원가입 성공 시 응답 데이터, 실패 시 에러 메시지
     */
    override suspend fun register(registerRequest: RegisterRequest): Result<RegisterResponse> {
        return handleApiCall(
            apiCall = { authApi.register(registerRequest) },
            noDataError = ERROR_REGISTER_NO_DATA,
            defaultError = ERROR_REGISTER_FAILED,
            handleErrorCode = { code ->
                when (code) {
                    STATUS_BAD_REQUEST -> ERROR_ALREADY_REGISTERED
                    STATUS_UNAUTHORIZED -> ERROR_UNAUTHORIZED
                    STATUS_FORBIDDEN -> "접근 권한이 없습니다"
                    else -> null
                }
            }
        )
    }

    /**
     * API 호출을 처리하고 응답을 적절히 변환하는 제네릭 함수
     * @param apiCall API 호출을 수행할 suspend 함수
     * @param noDataError 데이터가 없을 때 사용할 에러 메시지
     * @param defaultError 기본 에러 메시지
     * @param handleErrorCode HTTP 상태 코드에 따른 커스텀 에러 메시지를 처리하는 함수
     * @return Result<T> API 호출 결과를 담은 Result 객체
     */
    private suspend fun <T> handleApiCall(
        apiCall: suspend () -> retrofit2.Response<T>,  // ApiResponse wrapper 제거
        noDataError: String,
        defaultError: String,
        handleErrorCode: (Int) -> String?
    ): Result<T> {
        return try {
            val response = apiCall()
            val errorBody = response.errorBody()?.string()

            // 디버깅을 위한 로그 출력
            Log.d("Auth", "Response Code: ${response.code()}")
            Log.d("Auth", "Response Body: ${response.body()}")
            Log.d("Auth", "Error Body: $errorBody")
            Log.d("Auth", "Headers: ${response.headers()}")

            when (response.code()) {
                // 정상 응답 처리
                STATUS_OK -> {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception(noDataError))
                }
                // 회원가입 필요 상태 처리
                STATUS_NO_CONTENT -> {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        Result.failure(Exception(errorResponse?.message ?: ERROR_LOGIN_NO_DATA))
                    } else {
                        Result.failure(Exception(ERROR_LOGIN_NO_DATA))
                    }
                }
                // 잘못된 요청 처리
                STATUS_BAD_REQUEST -> {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        Result.failure(Exception(errorResponse?.message ?: ERROR_ALREADY_REGISTERED))
                    } else {
                        Result.failure(Exception(defaultError))
                    }
                }
                // 인증 실패 처리
                STATUS_UNAUTHORIZED -> {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        Result.failure(Exception(errorResponse?.message ?: ERROR_REGISTER_NO_DATA))
                    } else {
                        Result.failure(Exception(defaultError))
                    }
                }
                // 기타 에러 처리
                else -> {
                    val errorMessage = handleErrorCode(response.code()) ?: defaultError
                    Result.failure(Exception(errorMessage))
                }
            }
        } catch (e: Exception) {
            // API 호출 자체가 실패한 경우
            Result.failure(e)
        }
    }

    override suspend fun updateNickname(userId: Long, nickname: String): Result<Unit> {
        Log.d("MyPageRepository", "닉네임 변경 시도 - userId: $userId, nickname: $nickname")

        return handleApiCall(
            apiCall = {
                Log.d("MyPageRepository", "API 호출 시작")
                try {
                    val response = authApi.updateUserNickname(
                        userId = userId,
                        request = UpdateUserRequest(nickname = nickname)
                    )
                    Log.d("MyPageRepository", "API 응답 성공: $response")
                    response
                } catch (e: Exception) {
                    Log.e("MyPageRepository", "API 호출 실패", e)
                    Log.e("MyPageRepository", "에러 메시지: ${e.message}")
                    Log.e("MyPageRepository", "상세 스택트레이스: ${e.stackTraceToString()}")
                    throw e
                }
            },
            noDataError = "닉네임 변경 데이터가 없습니다.",
            defaultError = "닉네임 변경 실패",
            handleErrorCode = { code ->
                Log.d("MyPageRepository", "에러 코드 처리: $code")
                when (code) {
                    STATUS_BAD_REQUEST -> {
                        Log.e("MyPageRepository", "잘못된 닉네임 형식 (400)")
                        "잘못된 닉네임 형식입니다."
                    }
                    STATUS_UNAUTHORIZED -> {
                        Log.e("MyPageRepository", "인증 실패 (401)")
                        "인증에 실패했습니다."
                    }
                    else -> {
                        Log.e("MyPageRepository", "알 수 없는 에러 코드: $code")
                        null
                    }
                }
            }
        ).also { result ->
            when {
                result.isSuccess -> Log.d("MyPageRepository", "닉네임 변경 성공: ${result.getOrNull()}")
                result.isFailure -> Log.e("MyPageRepository", "닉네임 변경 최종 실패", result.exceptionOrNull())
            }
        }
    }

    override suspend fun withDraw(userId: Long): Result<Unit> {
        return handleApiCall(
            apiCall = { authApi.withDraw(userId) },
            noDataError = "회원 탈퇴 데이터가 없습니다.",
            defaultError = "회원 탈퇴 실패",
            handleErrorCode = { code ->
                when (code) {
                    STATUS_UNAUTHORIZED -> "인증에 실패했습니다."
                    else -> null
                }
            }
        )
    }

}