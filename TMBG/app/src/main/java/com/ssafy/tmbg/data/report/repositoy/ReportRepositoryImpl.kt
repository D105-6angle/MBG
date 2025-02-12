package com.ssafy.tmbg.data.report.repositoy

import android.util.Log
import com.google.gson.Gson
import com.ssafy.tmbg.api.ReportApi
import com.ssafy.tmbg.data.auth.response.ErrorResponse
import com.ssafy.tmbg.data.report.response.ReportResponse
import javax.inject.Inject

/**
 * 보고서 관련 Repository 구현체
 * API 호출 결과를 처리하고 적절한 응답을 반환하는 역할을 담당
 */
class ReportRepositoryImpl @Inject constructor(
    private val reportApi: ReportApi
) : ReportRepository {

    companion object {
        // HTTP 상태 코드 상수
        private const val STATUS_OK = 200
        private const val STATUS_NOT_FOUND = 404

        // 에러 메시지 상수
        private const val ERROR_NOT_FOUND = "존재하지 않는 방입니다"
    }

    /**
     * 특정 방의 보고서를 조회하는 메서드
     * @param roomId 조회할 방 ID
     * @return Result<ReportResponse> 성공 시 보고서 데이터, 실패 시 에러 정보를 포함한 Result 객체
     */
    override suspend fun getReport(roomId: Int): Result<ReportResponse> {
        return handleApiCall(
            apiCall = { reportApi.getReport(roomId = roomId) },
            noDataError = ERROR_NOT_FOUND,
            defaultError = ERROR_NOT_FOUND,
            handleErrorCode = { code ->
                when (code) {
                    STATUS_NOT_FOUND -> ERROR_NOT_FOUND
                    else -> null
                }
            }
        )
    }

    /**
     * API 호출을 처리하고 결과를 표준화된 형식으로 변환하는 범용 메서드
     *
     * @param apiCall API 호출을 수행할 suspend 함수
     * @param noDataError 응답 본문이 null일 때 사용할 에러 메시지
     * @param defaultError 기본 에러 메시지
     * @param handleErrorCode 특정 HTTP 상태 코드에 대한 커스텀 에러 메시지 처리 함수
     * @return Result<T> API 호출 결과를 포함하는 Result 객체
     */
    private suspend fun <T> handleApiCall(
        apiCall: suspend () -> retrofit2.Response<T>,
        noDataError: String,
        defaultError: String,
        handleErrorCode: (Int) -> String?
    ): Result<T> {
        return try {
            val response = apiCall()
            val errorBody = response.errorBody()?.string()

            // 디버깅을 위한 로그 출력
            Log.d("Report", "Response Code: ${response.code()}")
            Log.d("Report", "Response Body: ${response.body()}")
            Log.d("Report", "Error Body: $errorBody")
            Log.d("Report", "Headers: ${response.headers()}")

            when (response.code()) {
                // 성공적인 응답 처리
                STATUS_OK -> {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception(noDataError))
                }

                // 404 Not Found 에러 처리
                STATUS_NOT_FOUND -> {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        // 서버에서 전달한 에러 메시지 파싱
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        Result.failure(Exception(errorResponse?.message ?: ERROR_NOT_FOUND))
                    } else {
                        Result.failure(Exception(ERROR_NOT_FOUND))
                    }
                }

                // 그 외 HTTP 상태 코드에 대한 처리
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