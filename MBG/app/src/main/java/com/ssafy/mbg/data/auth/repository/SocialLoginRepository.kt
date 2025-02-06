package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.dto.SocialLoginResult

interface SocialLoginRepository {
    suspend fun login() : Result<SocialLoginResult>
}