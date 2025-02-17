package com.ssafy.tmbg.data.notice.repository

import android.util.Log
import com.google.android.gms.common.api.internal.TaskApiCall
import com.google.gson.Gson
import com.ssafy.tmbg.api.NoticeApi
import com.ssafy.tmbg.data.auth.response.ErrorResponse
import com.ssafy.tmbg.data.notice.dao.Notice
import com.ssafy.tmbg.data.notice.request.PushNoticeRequest
import com.ssafy.tmbg.data.notice.response.NoticeResponse
import retrofit2.Response
import javax.inject.Inject

/**
 * Notice 관련 API 요청을 처리하는 Repository 구현체
 */
class NoticeRepositoryImpl @Inject constructor(
    private val noticeApi: NoticeApi
) : NoticeRepository {
    override suspend fun getNotice(roomId: Long): Result<List<NoticeResponse>> {
        Log.d(TAG, "getNotice 호출됨: roomId = $roomId")
        return handleApiCall(
            apiCall = {
                Log.d(TAG, "알림 목록 API 호출 시도")
                noticeApi.getNotice(roomId)
            },
            noDataError = "알림 목록을 가져올 수 없습니다.",
            defaultError = "알림 조회 중 오류가 발생했습니다.",
            handleErrorCode = { code ->
                Log.d(TAG, "알림 목록 조회 에러 코드: $code")
                when (code) {
                    else -> null
                }
            }
        )
    }

    override suspend fun createNotice(
        roomId: Long,
        content: String
    ): Result<Unit> {
        Log.d(TAG, "createNotice 호출됨: roomId = $roomId, content = $content")
        return handleApiCall(
            apiCall = {
                Log.d(TAG, "알림 생성 API 호출 시도")
                noticeApi.createNotice(roomId, content)
            },
            noDataError = "알림을 생성할 수 없습니다.",
            defaultError = "알림 생성 중 오류가 발생했습니다.",
            handleErrorCode = { code ->
                Log.d(TAG, "알림 생성 에러 코드: $code")
                when (code) {
                    else -> null
                }
            }
        )
    }

    override suspend fun createSatisfactionNotice(
        roomId: Long
    ): Result<Unit> {
        Log.d(TAG, "createSatisfactionNotice 호출됨")
        return handleApiCall(
            apiCall = {
                Log.d(TAG, "만족도 알림 생성 API 호출 시도")
                noticeApi.createSatisfactionNotice(roomId)
            },
            noDataError = "만족도 알림을 생성할 수 없습니다.",
            defaultError = "만족도 알림 생성 중 오류가 발생했습니다.",
            handleErrorCode = { code ->
                Log.d(TAG, "만족도 알림 생성 에러 코드: $code")
                when (code) {
                    else -> null
                }
            }
        )
    }

    private suspend fun <T> handleApiCall(
        apiCall: suspend () -> Response<T>,
        noDataError: String,
        defaultError: String,
        handleErrorCode: (Int) -> String?
    ): Result<T> {
        return try {
            Log.d(TAG, "API 호출 시작")
            val response = apiCall()
            val errorBody = response.errorBody()?.string()

            Log.d(TAG, "API 응답 코드: ${response.code()}")
            Log.d(TAG, "응답 본문: ${response.body()}")
            Log.d(TAG, "에러 본문: $errorBody")
            Log.d(TAG, "응답 헤더: ${response.headers()}")

            when (response.code()) {
                STATUS_OK -> {
                    Log.d(TAG, "API 호출 성공 (200)")
                    response.body()?.let {
                        Log.d(TAG, "응답 데이터 존재")
                        Result.success(it)
                    } ?: run {
                        Log.e(TAG, "응답 본문 없음: $noDataError")
                        Result.failure(Exception(noDataError))
                    }
                }

                STATUS_NOT_FOUND -> {
                    Log.e(TAG, "리소스를 찾을 수 없음 (404)")
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        Log.d(TAG, "에러 응답 본문 존재: $errorBody")
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        Result.failure(Exception(errorResponse?.message ?: ERROR_NOT_FOUND))
                    } else {
                        Log.d(TAG, "에러 응답 본문 없음")
                        Result.failure(Exception(ERROR_NOT_FOUND))
                    }
                }

                STATUS_FORBIDDEN -> {
                    Log.e(TAG, "권한 없음 (403)")
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        Log.d(TAG, "에러 응답 본문 존재: $errorBody")
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        Result.failure(Exception(errorResponse?.message ?: ERROR_FORBIDDEN))
                    } else {
                        Log.d(TAG, "에러 응답 본문 없음")
                        Result.failure(Exception(ERROR_FORBIDDEN))
                    }
                }

                else -> {
                    Log.e(TAG, "기타 에러 발생 (${response.code()})")
                    val errorMessage = handleErrorCode(response.code()) ?: defaultError
                    Result.failure(Exception(errorMessage))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "API 호출 중 예외 발생", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "NoticeRepository"
        private const val STATUS_OK = 200
        private const val STATUS_NOT_FOUND = 404
        private const val STATUS_FORBIDDEN = 403

        private const val ERROR_NOT_FOUND = "리소스를 찾을 수 없습니다."
        private const val ERROR_FORBIDDEN = "권한이 없습니다."
    }
}