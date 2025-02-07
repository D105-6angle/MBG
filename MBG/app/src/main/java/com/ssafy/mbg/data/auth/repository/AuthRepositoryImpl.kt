package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.api.AuthApi
import com.ssafy.mbg.data.auth.common.ApiResponse
import com.ssafy.mbg.data.auth.dto.LoginRequest
import com.ssafy.mbg.data.auth.dto.LoginResponse
import com.ssafy.mbg.data.auth.dto.RegisterRequest
import com.ssafy.mbg.data.auth.dto.RegisterResponse
import javax.inject.Inject

/**
 * 인증 관련 Repository 구현체
 * 로그인과 회원가입 요청을 처리하고 결과를 반환합니다.
 *
 * @property authApi 인증 관련 API 호출을 위한 인터페이스
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    companion object {
        // HTTP 상태 코드 정의
        private const val STATUS_NO_CONTENT = 204  // 요청은 성공했으나 반환할 데이터가 없음 (회원가입 필요)
        private const val STATUS_BAD_REQUEST = 400 // 잘못된 요청

        // 에러 메시지 정의
        private const val ERROR_LOGIN_NO_DATA = "회원가입이 필요합니다."
        private const val ERROR_REGISTER_NO_DATA = "회원가입 데이터가 없습니다."
        private const val ERROR_BAD_REQUEST = "잘못된 요청입니다."
        private const val ERROR_ALREADY_REGISTERED = "이미 가입된 회원입니다."
        private const val ERROR_LOGIN_FAILED = "로그인 실패"
        private const val ERROR_REGISTER_FAILED = "회원가입 실패"
    }

    /**
     * 로그인 요청을 처리합니다.
     *
     * @param loginRequest 로그인에 필요한 정보를 담은 요청 객체
     * @return
     * - 성공: Result.success(LoginResponse)
     * - 실패: Result.failure(Exception) 다음 상황에 따른 에러 메시지 포함
     *   - 204: 회원가입 필요
     *   - 400: 잘못된 요청
     */
    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return handleApiCall(
            apiCall = { authApi.login(loginRequest) },
            noDataError = ERROR_LOGIN_NO_DATA,
            defaultError = ERROR_LOGIN_FAILED,
            handleErrorCode = { code ->
                when (code) {
                    STATUS_NO_CONTENT -> ERROR_LOGIN_NO_DATA  // 회원가입으로 리다이렉션 필요
                    STATUS_BAD_REQUEST -> ERROR_BAD_REQUEST   // 요청 형식이 잘못됨
                    else -> null
                }
            }
        )
    }

    /**
     * 회원가입 요청을 처리합니다.
     *
     * @param registerRequest 회원가입에 필요한 정보를 담은 요청 객체
     * @return
     * - 성공: Result.success(RegisterResponse)
     * - 실패: Result.failure(Exception) 다음 상황에 따른 에러 메시지 포함
     *   - 400: 이미 가입된 회원
     */
    override suspend fun register(registerRequest: RegisterRequest): Result<RegisterResponse> {
        return handleApiCall(
            apiCall = { authApi.register(registerRequest) },
            noDataError = ERROR_REGISTER_NO_DATA,
            defaultError = ERROR_REGISTER_FAILED,
            handleErrorCode = { code ->
                when (code) {
                    STATUS_BAD_REQUEST -> ERROR_ALREADY_REGISTERED  // 이미 존재하는 회원
                    else -> null
                }
            }
        )
    }

    /**
     * API 호출을 처리하고 결과를 Result 객체로 변환하는 일반화된 함수입니다.
     *
     * @param apiCall API 호출을 수행하는 suspend 함수
     * @param noDataError 응답 데이터가 없을 때 사용할 에러 메시지
     * @param defaultError 기본 에러 메시지
     * @param handleErrorCode HTTP 상태 코드에 따른 커스텀 에러 메시지를 반환하는 함수
     * @return API 호출 결과를 포함한 Result 객체
     * - 성공: Result.success(T)
     * - 실패: Result.failure(Exception)
     *   - API 응답 성공했으나 데이터 없음: noDataError
     *   - API 응답 실패: handleErrorCode에서 정의된 에러 또는 defaultError
     *   - 예외 발생: 발생한 예외
     */
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