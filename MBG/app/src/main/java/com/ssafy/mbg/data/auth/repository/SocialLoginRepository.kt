package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.dao.SocialUserInfo

interface SocialLoginRepository {
    suspend fun login() : Result<SocialUserInfo>
}