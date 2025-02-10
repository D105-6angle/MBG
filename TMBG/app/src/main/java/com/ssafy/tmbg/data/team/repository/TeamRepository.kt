package com.ssafy.tmbg.data.team.repository

import com.ssafy.tmbg.api.TeamApi
import com.ssafy.tmbg.data.team.dao.Team
import com.ssafy.tmbg.data.team.dao.TeamCreateResponse
import com.ssafy.tmbg.data.team.dao.TeamRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepository @Inject constructor(
    private val teamApi: TeamApi
) {
    /** 팀 정보 조회 */
    suspend fun getTeam(roomId: Int): Response<Team> {
        return teamApi.getTeam(roomId)
    }

    /** 팀 생성 */
    suspend fun createTeam(teamRequest: TeamRequest): Response<TeamCreateResponse> {
        return teamApi.createTeam(teamRequest)
    }
}