//package com.ssafy.tmbg.data.auth.repository
//
//import com.ssafy.tmbg.data.auth.DAO.SocialUserInfo
//import com.ssafy.tmbg.data.auth.DAO.TokenResponse
//import com.ssafy.tmbg.data.auth.DAO.User
//
//// 전체 인증 처리를 위한 인터페이스
//interface AuthRepository {
//    // 소셜 로그인 정보로 인증 수행, 사용자 정보 반환
//    suspend fun signInWithSocial(socialUserInfo: SocialUserInfo) : Result<User>
//
//    // 카카오 로그인 수행 및 서버 인증 완료 후 사용자 정보 반환
//    suspend fun loginWithKakao() : Result<User>
//
//    // 네이버 로그인 수행 및 서버 인증 완료 후 사용자 정보 반환
//    suspend fun loginWithNaver(): Result<User>
//
//    // Refresh token으로 새로운 access token 발급 요청
//    suspend fun refreshToken(refreshToken: String): Result<TokenResponse>
//
//    // 로그아웃
//    suspend fun logout()
//}