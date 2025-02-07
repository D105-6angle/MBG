package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.dto.SocialUserInfo

interface SocialLoginRepository {
    suspend fun login() : Result<SocialUserInfo>
}