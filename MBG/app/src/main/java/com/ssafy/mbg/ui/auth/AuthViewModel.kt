package com.ssafy.mbg.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.mbg.data.auth.repository.KakaoLoginRepositoryImpl
import com.ssafy.mbg.data.auth.repository.NaverLoginRepositoryImpl
import com.ssafy.mbg.data.auth.repository.PastKakaoLoginRepositoryImpl
import com.ssafy.mbg.data.auth.repository.PastNaverLoginRepositoryImpl
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val pastKakaoLoginRepositoryImpl: PastKakaoLoginRepositoryImpl,
    private val pastNaverLoginRepositoryImpl: PastNaverLoginRepositoryImpl
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState : StateFlow<AuthState> = _authState.asStateFlow()

    fun handleKakaoLogin() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            pastKakaoLoginRepositoryImpl.login()
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

    fun handleNaverLogin(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
                override fun onSuccess() {
                    viewModelScope.launch {
                        try {
                            val result = pastNaverLoginRepositoryImpl.login()
                            result.onSuccess { userInfo ->
                                _authState.value = AuthState.NeedSignUp(
                                    email = userInfo.email,
                                    name = userInfo.name,
                                    socialId = "Naver" + userInfo.providerId
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