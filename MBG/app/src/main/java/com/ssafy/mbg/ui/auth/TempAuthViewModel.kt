package com.ssafy.mbg.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.mbg.data.auth.dto.LoginRequest
import com.ssafy.mbg.data.auth.dto.RegisterRequest
import com.ssafy.mbg.data.auth.repository.AuthRepository
import com.ssafy.mbg.data.auth.repository.KakaoLoginRepositoryImpl
import com.ssafy.mbg.data.auth.repository.NaverLoginRepositoryImpl
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
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun handleKakaoLogin() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            kakaoLoginRepositoryImpl.login()
                .onSuccess { result ->
                    // 로그인 시도
                    val loginRequest = LoginRequest(
                        providerId = "kakao${result.socialUserInfo.providerId}"
                    )
                    try {
                        authRepository.login(loginRequest)
                            .onSuccess { loginResponse ->
                                // 로그인 성공시 메인으로 이동
                                _authState.value = AuthState.NavigateToMain
                            }
                            .onFailure { exception ->
                                if (exception.message?.contains("400") == true) {
                                    // 미가입 회원이므로 회원가입 화면으로
                                    _authState.value = AuthState.NeedSignUp(
                                        email =result.socialUserInfo.email,
                                        name = result.socialUserInfo.name,
                                        socialId = "kakao${result.socialUserInfo.providerId}"
                                    )
                                } else {
                                    _authState.value =
                                        AuthState.Error(exception.message ?: "로그인 실패")
                                }
                            }
                    } catch (e: Exception) {
                        _authState.value = AuthState.Error(e.message ?: "로그인 실패")
                    }
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "카카오 로그인 실패..")
                }
        }
    }
    fun handleNaverLogin(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
                override fun onSuccess() {
                    viewModelScope.launch {
                        try {
                            val result = naverLoginRepositoryImpl.login()
                            result.onSuccess { socialLoginResult ->
                                // 로그인 시도
                                val loginRequest = LoginRequest(
                                    providerId = "naver${socialLoginResult.socialUserInfo.providerId}"
                                )
                                try {
                                    authRepository.login(loginRequest)
                                        .onSuccess { loginResponse ->
                                            // 로그인 성공시 메인으로 이동
                                            _authState.value = AuthState.NavigateToMain
                                        }
                                        .onFailure { exception ->
                                            if (exception.message?.contains("400") == true) {
                                                // 미가입 회원이므로 회원가입 화면으로
                                                _authState.value = AuthState.NeedSignUp(
                                                    email = socialLoginResult.socialUserInfo.email,
                                                    name = socialLoginResult.socialUserInfo.name,
                                                    socialId = "naver${socialLoginResult.socialUserInfo.providerId}"
                                                )
                                            } else {
                                                _authState.value = AuthState.Error(exception.message ?: "로그인 실패")
                                            }
                                        }
                                } catch (e: Exception) {
                                    _authState.value = AuthState.Error(e.message ?: "로그인 실패")
                                }
                            }.onFailure { exception ->
                                _authState.value = AuthState.Error(exception.message ?: "알 수 없는 오류가 발생했습니다")
                            }
                        } catch (e: Exception) {
                            _authState.value = AuthState.Error("네이버 로그인 중 오류가 발생했습니다")
                        }
                    }
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    _authState.value = AuthState.Error("네이버 로그인 실패: $message")
                }

                override fun onError(errorCode: Int, message: String) {
                    _authState.value = AuthState.Error("네이버 로그인 오류: $message")
                }
            })
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
                        // 회원가입 성공 후 자동 로그인
                        val loginRequest = LoginRequest(providerId = socialId)
                        authRepository.login(loginRequest)
                            .onSuccess {
                                _authState.value = AuthState.NavigateToMain
                            }
                            .onFailure { exception ->
                                _authState.value = AuthState.Error(exception.message ?: "로그인 실패")
                            }
                    }
                    .onFailure { exception ->
                        _authState.value = AuthState.Error(exception.message ?: "회원가입 실패")
                    }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "회원가입 실패")
            }
        }
    }
}