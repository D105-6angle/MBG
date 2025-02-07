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

/**
 * 인증 관련 ViewModel
 * 소셜 로그인(카카오, 네이버)과 회원가입을 처리
 */
@HiltViewModel
class TempAuthViewModel @Inject constructor(
    private val kakaoLoginRepositoryImpl: KakaoLoginRepositoryImpl,
    private val naverLoginRepositoryImpl: NaverLoginRepositoryImpl,
    private val authRepository: AuthRepository
) : ViewModel() {

    // 인증 상태를 관리하는 StateFlow
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // 에러 메시지 상수
    companion object {
        private const val ERROR_LOGIN_FAILED = "로그인 실패"
        private const val ERROR_REGISTER_FAILED = "회원가입 실패"
        private const val ERROR_UNKNOWN = "알 수 없는 오류가 발생했습니다"
        private const val ERROR_NAVER_LOGIN = "네이버 로그인 중 오류가 발생했습니다"
        private const val ERROR_KAKAO_LOGIN = "카카오 로그인 실패"
    }

    /**
     * 카카오 로그인 처리
     * 카카오 SDK를 통해 로그인을 시도하고 성공시 서버 로그인 진행
     */
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

    /**
     * 네이버 로그인 처리
     * 네이버 SDK를 통해 로그인을 시도
     * @param context 네이버 로그인 화면 표시를 위한 컨텍스트
     */
    fun handleNaverLogin(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            NaverIdLoginSDK.authenticate(context, getNaverLoginCallback())
        }
    }

    /**
     * 네이버 로그인 콜백 생성
     * 로그인 성공, 실패, 에러 상황 처리
     */
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

    /**
     * 네이버 로그인 성공 처리
     * 사용자 정보를 가져와 서버 로그인 진행
     */
    private suspend fun handleNaverLoginSuccess() {
        try {
            val result = naverLoginRepositoryImpl.login()
            result.onSuccess { socialLoginResult ->
                handleSocialLogin(
                    socialId = "naver${socialLoginResult.providerId}",
                    email = socialLoginResult.email,
                    name = socialLoginResult.name
                )
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: ERROR_UNKNOWN)
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(ERROR_NAVER_LOGIN)
        }
    }

    /**
     * 소셜 로그인 공통 처리
     * 서버에 로그인 요청을 보내고 결과에 따라 상태 업데이트
     * @param socialId 소셜 로그인 제공자 ID (e.g., "kakao123", "naver456")
     * @param email 사용자 이메일
     * @param name 사용자 이름
     */
    private suspend fun handleSocialLogin(
        socialId: String,
        email: String,
        name: String
    ) {
        try {
            val loginRequest = LoginRequest(providerId = socialId)
            authRepository.login(loginRequest)
                .onSuccess {
                    _authState.value = AuthState.NavigateToMain
                }
                .onFailure { exception ->
                    if (exception.message?.contains("400") == true) {
                        _authState.value = AuthState.NeedSignUp(
                            email = email,
                            name = name,
                            socialId = socialId
                        )
                    } else {
                        _authState.value = AuthState.Error(exception.message ?: ERROR_LOGIN_FAILED)
                    }
                }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: ERROR_LOGIN_FAILED)
        }
    }

    /**
     * 회원가입 처리
     * 서버에 회원가입 요청을 보내고 성공시 자동 로그인 진행
     * @param email 사용자 이메일
     * @param name 사용자 이름
     * @param socialId 소셜 로그인 제공자 ID
     * @param nickname 사용자가 설정한 닉네임
     */
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