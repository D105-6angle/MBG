package com.ssafy.mbg.api

import com.ssafy.mbg.data.home.dao.JoinGroupRequest
import com.ssafy.mbg.data.home.dao.JoinGroupResponse
import com.ssafy.mbg.data.home.dao.JoinRoomRequest
import com.ssafy.mbg.data.home.dao.JoinRoomResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HomeApi {

    // 초대코드를 통해 방 입장
    @POST("rooms/join")
    suspend fun joinRooms(
        @Body request: JoinRoomRequest
    ) : Response<JoinRoomResponse>

    // 조 선택하여 소속 저장
    @POST("rooms/{roomId}/groups/select")
    suspend fun joinGroups(
        @Path("roomId") roomId: Long,
        @Body request: JoinGroupRequest
    ) : Response<JoinGroupResponse>

    /** 조원 삭제 */
    @DELETE("rooms/{roomId}/groups/{groupNo}/members/{userId}")
    suspend fun deleteMember(
        @Path("roomId") roomId: Int,
        @Path("groupNo") groupNo: Int,
        @Path("userId") userId: Long
    ): Response<Unit>
}