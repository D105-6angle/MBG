package com.ssafy.tmbg.data.auth.repository

import com.ssafy.tmbg.data.auth.dao.SocialUserInfo


// 소셜 로그인 처리를 위한 인터 페이스
interface SocialLoginRepository {
    suspend fun login(): Result<SocialUserInfo>
}