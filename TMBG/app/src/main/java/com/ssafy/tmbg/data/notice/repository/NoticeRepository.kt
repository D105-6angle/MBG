package com.ssafy.tmbg.data.notice.repository

import com.ssafy.tmbg.data.notice.request.PushNoticeRequest
import com.ssafy.tmbg.data.notice.response.NoticeResponse
import retrofit2.Response

interface NoticeRepository {
    suspend fun getNotice(roomId: Long): Result<List<NoticeResponse>>  // Result로 감싸서 에러 처리
    suspend fun createNotice(roomId: Long, content: String): Result<Unit>
    suspend fun createSatisfactionNotice(roomId: Long): Result<Unit>
}