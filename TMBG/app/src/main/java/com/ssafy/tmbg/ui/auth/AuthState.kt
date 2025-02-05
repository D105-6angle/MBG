package com.ssafy.tmbg.ui.auth

// AuthState.kt - 인증 상태를 나타내는 sealed class
sealed class AuthState {
    data object Initial : AuthState()              // 초기 상태
    data object Loading : AuthState()              // 로딩 중
    data class Error(val message: String) : AuthState()  // 에러 상태
    data class NeedSignUp(                         // 회원가입 필요 상태
        val email : String,
        val name : String,
        val socialId : String
    ) : AuthState()
    data class Success(val accessToken : String) : AuthState()  // 로그인 성공
}