package com.ssafy.tmbg.data.profile.repository

import com.ssafy.tmbg.api.ProfileApi
import com.ssafy.tmbg.data.profile.response.ProfileResponse
import retrofit2.Response
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi : ProfileApi
) : ProfileRepository {
    override suspend fun getProgile(userId: Long, roomId: Long?): Response<ProfileResponse> {
        return profileApi.getProfile(userId, roomId)
    }
}