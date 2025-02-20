package com.ssafy.mbg.data.mypage.repository

import com.ssafy.mbg.data.auth.response.UserResponse
import com.ssafy.mbg.data.mypage.response.ProblemResponse
import com.ssafy.mbg.data.mypage.response.ProfileResponse
import retrofit2.Response

interface MyPageRepository {
    /**
     * 사용자 정보를 조회하는 함수
     *
     * @param userId 조회할 사용자의 ID
     * @return Response<UserResponse> 사용자 정보를 포함한 응답
     */
    suspend fun getProfile(userId: Long, roomId :Long?): Response<ProfileResponse>

    /**
     * 사용자가 푼 특정 문제의 상세 기록을 조회하는 함수
     *
     * @param userId 사용자 ID
     * @param logId 조회할 문제 풀이 기록의 ID
     * @return Response<ProblemResponse> 문제 풀이 상세 정보를 포함한 응답
     */
    suspend fun getDetailProblem(userId: Long, cardId: Long): Response<ProblemResponse>
}