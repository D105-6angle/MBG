package com.ssafy.tmbg.api

import com.ssafy.tmbg.data.team.dao.Team
import com.ssafy.tmbg.data.team.dao.TeamCreateResponse
import com.ssafy.tmbg.data.team.dao.TeamRequest
import com.ssafy.tmbg.data.team.dao.GroupDetailResponse
import com.ssafy.tmbg.data.team.dao.TeamResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface TeamApi {

    /** 생성된 방을 조회*/
    @GET("rooms/{roomId}")
    suspend fun getTeam(@Path("roomId") roomId: Int): Response<TeamResponse>

    /**방 생성*/
    @POST("rooms")
    suspend fun createTeam(@Body teamRequest: TeamRequest): Response<TeamCreateResponse>
    
    /** 그룹 상세 정보 조회 */
    @GET("rooms/{roomId}/groups/{groupNo}")
    suspend fun getGroupDetail(
        @Path("roomId") roomId: Int,
        @Path("groupNo") groupNo: Int
    ): Response<GroupDetailResponse>
}