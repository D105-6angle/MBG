package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.DAO.SocialUserInfo

interface SocialLoginRepository {
    suspend fun login() : Result<SocialUserInfo>
}