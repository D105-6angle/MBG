package com.ssafy.tmbg.api

import com.ssafy.tmbg.data.team.dao.Team
import com.ssafy.tmbg.data.team.dao.TeamCreateResponse
import com.ssafy.tmbg.data.team.dao.TeamRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface TeamApi {

    /** 생성된 방을 조회*/
    @GET("rooms/{roomId}")
    suspend fun getTeam(@Path("roomId") roomId:Int): Response<Team>

    /**방 생성*/
    @POST("rooms")
    suspend fun createTeam(@Body teamRequest: TeamRequest): Response<TeamCreateResponse>

}