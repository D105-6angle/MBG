package com.ssafy.tmbg.data.auth.repository

import com.ssafy.tmbg.data.auth.DAO.SocialUserInfo

interface KakaoLoginRepository {
    suspend fun login(): Result<SocialUserInfo>
}