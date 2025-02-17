package com.ssafy.tmbg.data.profile.repository

import com.ssafy.tmbg.data.profile.response.ProfileResponse
import retrofit2.Response

interface ProfileRepository {
    suspend fun getProgile(userId: Long, roomId : Long?) : Response<ProfileResponse>

}