package com.ssafy.mbg.data.home.repository

import com.ssafy.mbg.api.HomeApi
import com.ssafy.mbg.data.home.dao.JoinGroupRequest
import com.ssafy.mbg.data.home.dao.JoinGroupResponse
import com.ssafy.mbg.data.home.dao.JoinRoomRequest
import com.ssafy.mbg.data.home.dao.JoinRoomResponse
import retrofit2.Response
import javax.inject.Inject


class HomeRepository @Inject constructor(
    private val _home: HomeApi
) {
    suspend fun joinRoom(joinRoomRequest: JoinRoomRequest): Response<JoinRoomResponse> {
        return _home.joinRooms(joinRoomRequest)
    }

    suspend fun joinGroup(
        roomId: Long,
        joinGroupRequest: JoinGroupRequest
    ): Response<JoinGroupResponse> {
        return _home.joinGroups(roomId, joinGroupRequest)
    }

    /** 조원 삭제 */
    suspend fun deleteMember(roomId: Int, groupNo: Int, userId: Long): Response<Unit> {
        return _home.deleteMember(roomId, groupNo, userId)
    }
}