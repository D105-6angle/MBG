package com.ssafy.tmbg.ui.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.tmbg.data.auth.repository.AuthRepository
import com.ssafy.tmbg.data.auth.repository.KakaoLoginRepositoryImpl
import com.ssafy.tmbg.data.auth.repository.NaverLoginRepositoryImpl
import com.ssafy.tmbg.data.auth.request.LoginRequest
import com.ssafy.tmbg.data.auth.request.RegisterRequest
import com.ssafy.tmbg.di.ServerTokenManager
import com.ssafy.tmbg.di.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// AuthViewModel.kt - 인증 관련 ViewModel
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val kakaoLoginRepositoryImpl: KakaoLoginRepositoryImpl,
    private val naverLoginRepositoryImpl: NaverLoginRepositoryImpl,
    private val authRepository: AuthRepository,
    private val serverTokenManager: ServerTokenManager,
    private val userPreferences: UserPreferences,
    @ApplicationContext private val context: Context

) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    val TAG = "Login"

    // 에러 코드에 해당하는 에러 메시지 상수 값
    companion object {
        private const val ERROR_LOGIN_FAILED = "로그인 실패"
        private const val ERROR_REGISTER_FAILED = "회원가입 실패"
        private const val ERROR_UNKNOWN = "알 수 없는 오류가 발생했습니다"
        private const val ERROR_NAVER_LOGIN = "네이버 로그인 중 오류가 발생했습니다"
        private const val ERROR_KAKAO_LOGIN = "카카오 로그인 실패"
    }


    // 카카오 로그인 진행
    fun handleKakaoLogin() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            kakaoLoginRepositoryImpl.login()
                // 카카오 로그인이 성공이라면
                .onSuccess { result ->
                    // 카카오에서 발급받은 정보로 handleSocialLogin 함수 호출
                    handleSocialLogin(
                        socialId = "kakao${result.providerId}",
                        email = result.email,
                        name = result.name
                    )
                }
                // 에러일 경우, 에러 메시지 설정
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: ERROR_KAKAO_LOGIN)
                }
        }
    }

    // 네이버 로그인
    fun handleNaverLogin(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            NaverIdLoginSDK.authenticate(context, getNaverLoginCallback())
        }
    }

    // 네이버 로그인 콜백 함수
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


    // 네이버 로그인이 성공적일 때
    private suspend fun handleNaverLoginSuccess() {
        try {
            naverLoginRepositoryImpl.login()
                // 네이버에서 발급받은 정보로 handleSocialLogin 호출
                .onSuccess { result ->
                    handleSocialLogin(
                        socialId = "naver${result.providerId}",
                        email = result.email,
                        name = result.name
                    )
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: ERROR_UNKNOWN)
                }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(ERROR_NAVER_LOGIN)
        }
    }
    // 서버와 로그인 하는 함수
    private suspend fun handleSocialLogin(
        socialId: String,
        email: String,
        name: String
    ) {
        Log.d(TAG, "소셜 로그인 시작: socialId=$socialId, email=$email, name=$name")
        try {
            // request 값으로 providerId 설정
            val loginRequest = LoginRequest(providerId = socialId)
            Log.d(TAG, socialId)
            // 로그인 경로로 요청을 담아서 보냄
            authRepository.login(loginRequest)
                .onSuccess { response ->
                    // 성공했다면
                    // 토큰과, 소셜 아이디 클라이언트에 저장
                    serverTokenManager.saveToken(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                    serverTokenManager.saveProviderId(
                        providerId = socialId
                    )
                    userPreferences.userId = response.userId
                    // 메인 액티비티로 이동
                    _authState.value = AuthState.NavigateToMain
                }
                // 실패일 경우
                .onFailure { exception ->
                    Log.e(TAG, exception.message?:"")
                    // 만약 204 에러를 반환했다면
                    if (exception.message?.contains("회원가입이 필요합니다") == true ||
                        exception.message?.contains("로그인 실패") == true ||
                        exception.message?.contains("탈퇴한 회원") == true) {
                        // 회원가입으로 이동 이때 소셜 로그인에서 받은 정보를 들고감
                        _authState.value = AuthState.NeedSignUp(
                            email = email,
                            name = name,
                            socialId = socialId
                        )
                        // 다른 에러라면 에러 메시지 설정
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
    // 서버 회원가입
    fun register(email: String, name: String, socialId: String, nickname: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // 회원가입에 요청에 필요한 정보를 담읆
                val registerRequest = RegisterRequest(
                    providerId = socialId,
                    email = email,
                    name = name,
                    nickname = nickname
                )
                Log.d(TAG, "회원가입 시작: socialId=$socialId, email=$email, name=$name, nickname=$nickname")

                // 요청 정보를 담아서 회원가입 함수  호출
                authRepository.register(registerRequest)
                    .onSuccess { response ->
                        // 응답에서 토큰 값이 넘어오므로, 굳이 로그인 함수 재호출 필요 없이 바로 토큰 저장하고 메인으로 이동
                        Log.d(TAG, "회원가입 성공 response: $response")
                        serverTokenManager.saveToken(refreshToken = response.refreshToken, accessToken = response.accessToken)
                        serverTokenManager.saveProviderId(socialId)  // providerId 저장
                        userPreferences.userId = response.userId

                        _authState.value = AuthState.NavigateToMain  // 바로 메인으로 이동
                    }
                    // 실패했다면 실페 에러 메시지 설정
                    .onFailure { exception ->
                        Log.e(TAG, "회원 가입 실패", exception)
                        _authState.value = AuthState.Error(exception.message ?: ERROR_REGISTER_FAILED)
                    }
                // 요청 조차 가지 못했다면 에러 메시지 설정
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: ERROR_REGISTER_FAILED)
            }
        }
    }
    // 자동 로그인(로그인 시에 저장 해놓은 providerId 값으로 자동 로그인 로직)
    fun checkAutoLogin() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // 중앙 저장소에 저장된 providerId를 가져옴
                val providerId = serverTokenManager.getProviderId()
                if (!providerId.isNullOrEmpty()) {
                    // providerId가 존재 하면 자동 로그인 시도
                    val loginRequest = LoginRequest(providerId = providerId)
                    authRepository.login(loginRequest)
                        .onSuccess { response ->
                            serverTokenManager.saveToken(
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken
                            )
                            _authState.value = AuthState.NavigateToMain
                        }
                        // 실패 라면, 에러 메시지 설정
                        .onFailure {

                            _authState.value = AuthState.Error("자동 로그인 실패")
                        }
                } else {
                    _authState.value = AuthState.Error("로그인이 필요합니다")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "자동 로그인 실패")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            serverTokenManager.clearAll()
            _authState.value = AuthState.NavigateToLogin
        }
    }

    fun withDraw() {
        viewModelScope.launch {
            Log.d("AuthViewModel", "회원 탈퇴 시작")
            _authState.value = AuthState.Loading

            try {
                val userId = userPreferences.userId
                Log.d("AuthViewModel", "회원 탈퇴 - userId: $userId")

                if(userId != null) {
                    Log.d("AuthViewModel", "회원 탈퇴 API 호출 시작")
                    authRepository.withDraw(userId)
                        .onSuccess {
                            Log.d("AuthViewModel", "회원 탈퇴 API 호출 성공")
                            serverTokenManager.clearAll()
                            Log.d("AuthViewModel", "토큰 삭제 완료")
                            userPreferences.userId = null
                            Log.d("AuthViewModel", "사용자 정보 삭제 완료")

                            _authState.value = AuthState.NavigateToLogin
                        }
                        .onFailure { exception ->
                            Log.e("AuthViewModel", "회원 탈퇴 실패", exception)
                            _authState.value = AuthState.Error(exception.message ?: "회원 탈퇴 실패")
                        }
                } else {
                    Log.e("AuthViewModel", "회원 탈퇴 실패 - userId가 null")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "회원 탈퇴 중 예외 발생", e)
                _authState.value = AuthState.Error("회원 탈퇴")
            }
        }
    }

    fun updateNickname(newNickname : String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = userPreferences.userId
                if(userId != null) {
                    authRepository.updateNickname(userId, newNickname)
                        .onSuccess {
                            _authState.value = AuthState.Success("닉네임이 변경되었습니다")
                        }
                        .onFailure { exception ->
                            _authState.value = AuthState.Error(exception.message ?: "닉네임 변경 실패 ")
                        }
                }
            } catch (e : Exception) {
                _authState.value = AuthState.Error("닉네임 변경 실패")
            }
        }
    }

}