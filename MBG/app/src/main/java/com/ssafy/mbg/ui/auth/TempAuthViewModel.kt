package com.ssafy.mbg.ui.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.mbg.data.auth.dto.LoginRequest
import com.ssafy.mbg.data.auth.dto.RegisterRequest
import com.ssafy.mbg.data.auth.repository.AuthRepository
import com.ssafy.mbg.data.auth.repository.KakaoLoginRepositoryImpl
import com.ssafy.mbg.data.auth.repository.NaverLoginRepositoryImpl
import com.ssafy.mbg.data.manger.ServerTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TempAuthViewModel @Inject constructor(
    private val kakaoLoginRepositoryImpl: KakaoLoginRepositoryImpl,
    private val naverLoginRepositoryImpl: NaverLoginRepositoryImpl,
    private val authRepository: AuthRepository,
    private val serverTokenManager: ServerTokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    val TAG = "Login"
    companion object {
        private const val ERROR_LOGIN_FAILED = "로그인 실패"
        private const val ERROR_REGISTER_FAILED = "회원가입 실패"
        private const val ERROR_UNKNOWN = "알 수 없는 오류가 발생했습니다"
        private const val ERROR_NAVER_LOGIN = "네이버 로그인 중 오류가 발생했습니다"
        private const val ERROR_KAKAO_LOGIN = "카카오 로그인 실패"
    }

    fun handleKakaoLogin() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            kakaoLoginRepositoryImpl.login()
                .onSuccess { result ->
                    handleSocialLogin(
                        socialId = "kakao${result.providerId}",
                        email = result.email,
                        name = result.name
                    )
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: ERROR_KAKAO_LOGIN)
                }
        }
    }

    fun handleNaverLogin(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            NaverIdLoginSDK.authenticate(context, getNaverLoginCallback())
        }
    }

    private fun getNaverLoginCallback() = object : OAuthLoginCallback {
        override fun onSuccess() {
            viewModelScope.launch {
                handleNaverLoginSuccess()
            }
        }

        override fun onFailure(httpStatus: Int, message: String) {
            _authState.value = AuthState.Error("네이버 로그인 실패: $message")
        }

        override fun onError(errorCode: Int, message: String) {
            _authState.value = AuthState.Error("네이버 로그인 오류: $message")
        }
    }

    private suspend fun handleNaverLoginSuccess() {
        try {
            naverLoginRepositoryImpl.login()
                .onSuccess { socialLoginResult ->
                    handleSocialLogin(
                        socialId = "naver${socialLoginResult.providerId}",
                        email = socialLoginResult.email,
                        name = socialLoginResult.name
                    )
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: ERROR_UNKNOWN)
                }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(ERROR_NAVER_LOGIN)
        }
    }

    private suspend fun handleSocialLogin(
        socialId: String,
        email: String,
        name: String
    ) {
        Log.d(TAG, "소셜 로그인 시작: socialId=$socialId, email=$email, name=$name")
        try {
            val loginRequest = LoginRequest(providerId = socialId)
            authRepository.login(loginRequest)
                .onSuccess { response ->
                    serverTokenManager.saveToken(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                    _authState.value = AuthState.NavigateToMain
                }
                .onFailure { exception ->
                    if (exception.message?.contains("회원가입이 필요합니다") == true) {
                        _authState.value = AuthState.NeedSignUp(
                            email = email,
                            name = name,
                            socialId = socialId
                        )
                    } else {
                        Log.e(TAG, "서버 로그인 실패", exception)
                        _authState.value = AuthState.Error(exception.message ?: ERROR_LOGIN_FAILED)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "소셜 로그인 예외 발생", e)
            _authState.value = AuthState.Error(e.message ?: ERROR_LOGIN_FAILED)
        }
    }

    fun register(email: String, name: String, socialId: String, nickname: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val registerRequest = RegisterRequest(
                    providerId = socialId,
                    email = email,
                    name = name,
                    nickname = nickname
                )

                authRepository.register(registerRequest)
                    .onSuccess {
                        handleSocialLogin(socialId, email, name)
                    }
                    .onFailure { exception ->
                        _authState.value = AuthState.Error(exception.message ?: ERROR_REGISTER_FAILED)
                    }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: ERROR_REGISTER_FAILED)
            }
        }
    }
}