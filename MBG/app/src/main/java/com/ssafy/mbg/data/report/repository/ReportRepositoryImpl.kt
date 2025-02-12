package com.ssafy.mbg.data.report.repository

import android.util.Log
import com.google.gson.Gson
import com.ssafy.mbg.api.ReportApi
import com.ssafy.mbg.data.auth.response.ErrorResponse
import com.ssafy.mbg.data.report.request.ReportRequest
import retrofit2.Response
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportApi: ReportApi
) : ReportRepository {

    companion object {
        private const val TAG = "ReportRepository"
        private const val STATUS_OK = 200
        private const val STATUS_BAD_REQUEST = 400
        private const val STATUS_UNAUTHORIZED = 401
        private const val STATUS_NOT_FOUND = 404
        private const val STATUS_INTERNAL_ERROR = 500

        private const val ERROR_NOT_FOUND = "정보를 찾을 수 없습니다."
        private const val ERROR_BAD_REQUEST = "잘못된 요청입니다."
        private const val ERROR_UNAUTHORIZED = "인증되지 않은 사용자입니다."
        private const val ERROR_SERVER = "서버 오류가 발생했습니다."
        private const val ERROR_DEFAULT = "알 수 없는 오류가 발생했습니다."
        private const val ERROR_NO_DATA = "응답 데이터가 없습니다."
    }

    override suspend fun createReport(request: ReportRequest): Result<Unit> {
        return handleApiCall(
            apiCall = {
                Log.d(TAG, "만족도 조사 요청 - request: $request")
                reportApi.createReport(request)
            },
            noDataError = ERROR_NO_DATA,
            defaultError = ERROR_DEFAULT
        ) { code ->
            when (code) {
                STATUS_BAD_REQUEST -> ERROR_BAD_REQUEST
                STATUS_UNAUTHORIZED -> ERROR_UNAUTHORIZED
                STATUS_NOT_FOUND -> ERROR_NOT_FOUND
                STATUS_INTERNAL_ERROR -> ERROR_SERVER
                else -> null
            }
        }
    }

    private suspend fun <T> handleApiCall(
        apiCall: suspend () -> Response<T>,
        noDataError: String,
        defaultError: String,
        handleErrorCode: (Int) -> String?
    ): Result<T> {
        return try {
            val response = apiCall()
            val errorBody = response.errorBody()?.string()

            Log.d(TAG, "Response Code: ${response.code()}")
            Log.d(TAG, "Response Body: ${response.body()}")
            Log.d(TAG, "Error Body: $errorBody")
            Log.d(TAG, "Headers: ${response.headers()}")

            when (response.code()) {
                STATUS_OK -> {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception(noDataError))
                }
                else -> {
                    val errorMessage = if (errorBody != null) {
                        try {
                            Gson().fromJson(errorBody, ErrorResponse::class.java).message
                        } catch (e: Exception) {
                            handleErrorCode(response.code()) ?: defaultError
                        }
                    } else {
                        handleErrorCode(response.code()) ?: defaultError
                    }
                    Result.failure(Exception(errorMessage))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "API 호출 실패", e)
            Result.failure(e)
        }
    }
}