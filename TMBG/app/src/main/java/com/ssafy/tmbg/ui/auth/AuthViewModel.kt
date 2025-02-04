package com.ssafy.tmbg.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tmbg.data.auth.repository.KakaoLoginRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val kakaoLoginRepositoryImpl: KakaoLoginRepositoryImpl
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState : StateFlow<AuthState> = _authState.asStateFlow()

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
}