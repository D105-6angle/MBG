package com.ssafy.tmbg.api

import com.ssafy.tmbg.data.profile.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApi {

    @GET("mypage/{userId}")
    suspend fun getProfile(
        @Path("userId") userId: Long,
        @Query("roomId") roomId : Long?
    ) : Response<ProfileResponse>
}