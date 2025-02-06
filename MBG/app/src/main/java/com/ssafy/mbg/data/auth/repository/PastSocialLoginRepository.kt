package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.dto.SocialUserInfo

interface PastSocialLoginRepository {
    suspend fun login() : Result<SocialUserInfo>
}