package com.ssafy.tmbg.api

import com.ssafy.tmbg.data.notice.request.PushNoticeRequest
import com.ssafy.tmbg.data.notice.response.NoticeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NoticeApi {
    @GET("notices/room/{roomId}")
    suspend fun getNotice(
        @Path("roomId") roomId: Long,
    ): Response<List<NoticeResponse>>  // List를 Response로 감싸도록 수정

    @POST("fcm/notice")
    suspend fun createNotice(
        @Query("roomId") roomId: Long,
        @Query("content") content: String,
    ): Response<Unit>

    @POST("fcm/send-survey")
    suspend fun createSatisfactionNotice(
        @Query("roomId") roomId: Long
    ): Response<Unit>
}