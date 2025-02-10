package com.ssafy.mbg.ui.auth

sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data object NavigateToLogin : AuthState()
    data class Success(val message: String) :AuthState()
    data class Error(val message : String) : AuthState()
    data class NeedSignUp(
        val email : String,
        val name : String,
        val socialId : String
    ) : AuthState()
    data object NavigateToMain : AuthState()
}