package com.ssafy.mbg.data.book.repository

import android.util.Log
import com.google.gson.Gson
import com.ssafy.mbg.api.BookApi
import com.ssafy.mbg.data.auth.response.ErrorResponse
import com.ssafy.mbg.data.book.response.BookResponse
import retrofit2.Response
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi
) : BookRepository {

    override suspend fun getBook(userId: Long): Result<BookResponse> {
        return handleApiCall(
            apiCall = { bookApi.getBook(userId) },
            noDataError = ERROR_NO_DATA,
            defaultError = ERROR_DEFAULT,
            handleErrorCode = { code ->
                when (code) {
                    STATUS_NOT_FOUND -> ERROR_NOT_FOUND
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
            val response = apiCall()
            val errorBody = response.errorBody()?.string()

            Log.d(TAG, "Response code: ${response.code()}")
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

    companion object {
        private const val TAG = "BookRepository"

        private const val STATUS_OK = 200
        private const val STATUS_NOT_FOUND = 404

        private const val ERROR_NOT_FOUND = "정보를 찾을 수 없습니다."
        private const val ERROR_NO_DATA = "데이터가 없습니다."
        private const val ERROR_DEFAULT = "오류가 발생했습니다."
    }
}