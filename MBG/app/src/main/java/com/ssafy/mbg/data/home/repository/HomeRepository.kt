package com.ssafy.mbg.data.home.repository

import com.ssafy.mbg.api.HomeApi
import com.ssafy.mbg.data.home.dao.JoinRequest
import com.ssafy.mbg.data.home.dao.JoinResponse
import retrofit2.Response
import javax.inject.Inject


class HomeRepository @Inject constructor(
    private val _home : HomeApi
){
    suspend fun joinRoom(joinRequest: JoinRequest ) :Response<JoinResponse> {
        return _home.joinRooms(joinRequest)
    }
}