package com.ssafy.mbg.api

import com.ssafy.mbg.data.home.dao.JoinRequest
import com.ssafy.mbg.data.home.dao.JoinResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HomeApi {

    // 초대코드를 통해 방 입장
    @POST("rooms/join")
    suspend fun joinRooms(
        @Body request: JoinRequest
    ) : Response<JoinResponse>

}