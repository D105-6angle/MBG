package com.ssafy.tmbg.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.tmbg.data.auth.repository.KakaoLoginRepositoryImpl
import com.ssafy.tmbg.data.auth.repository.NaverLoginRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// AuthViewModel.kt - 인증 관련 ViewModel
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val kakaoLoginRepositoryImpl: KakaoLoginRepositoryImpl,
    private val naverLoginRepositoryImpl: NaverLoginRepositoryImpl
) : ViewModel() {
    // 인증 상태를 관리하는 StateFlow
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState : StateFlow<AuthState> = _authState.asStateFlow()

    // 카카오 로그인 처리 함수
    fun handleKakaoLogin() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            kakaoLoginRepositoryImpl.login()
                .onSuccess { userInfo ->
                    _authState.value = AuthState.NeedSignUp(
                        email = userInfo.email,
                        name = userInfo.name,
                        socialId = "kakao" + userInfo.providerId
                    )
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "카카오 로그인 실패..")
                }
        }
    }

    // 네이버 로그인 처리 함수
    fun handleNaverLogin(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
                override fun onSuccess() {
                    viewModelScope.launch {
                        try {
                            val result = naverLoginRepositoryImpl.login()
                            result.onSuccess { userInfo ->
                                _authState.value = AuthState.NeedSignUp(
                                    email = userInfo.email,
                                    name = userInfo.name,
                                    socialId = userInfo.providerId
                                )
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
}