//package com.ssafy.mbg.ui.auth
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.ssafy.mbg.data.auth.dto.LoginRequest
//import com.ssafy.mbg.data.auth.repository.AuthRepository
//import com.ssafy.mbg.data.auth.repository.KakaoLoginRepositoryImpl
//import com.ssafy.mbg.data.auth.repository.NaverLoginRepositoryImpl
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class TempAuthViewModel @Inject constructor(
//    private val kakaoLoginRepositoryImpl: KakaoLoginRepositoryImpl,
//    private val naverLoginRepositoryImpl: NaverLoginRepositoryImpl,
//    private val authRepository: AuthRepository
//) : ViewModel() {
//    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
//    val authState: StateFlow<AuthState> = _authState.asStateFlow()
//
//    fun handleKakaoLogin() {
//        viewModelScope.launch {
//            _authState.value = AuthState.Loading
//            kakaoLoginRepositoryImpl.login()
//                .onSuccess { result ->
//                    // 로그인 시도
//                    val loginRequest = LoginRequest(
//                        providerId = "kakao${result.socialUserInfo.providerId}"
//                    )
//                    try {
//                        authRepository.login(loginRequest)
//                            .onSuccess { loginResponse ->
//                                // 로그인 성공시 메인으로 이동
//                                _authState.value = AuthState.NavigateToMain
//                            }
//                            .onFailure { exception ->
//                                if (exception.message?.contains("400") == true) {
//                                    // 미가입 회원이므로 회원가입 화면으로
//                                    _authState.value = AuthState.NeedSignUp(
//                                        email = result.socialUserInfo.email,
//                                        name = result.socialUserInfo.name,
//                                        socialId = "kakao${result.socialUserInfo.providerId}"
//                                    )
//                                } else {
//                                    _authState.value =
//                                        AuthState.Error(exception.message ?: "로그인 실패")
//                                }
//                            }
//                    } catch (e: Exception) {
//                        _authState.value = AuthState.Error(e.message ?: "로그인 실패")
//                    }
//                }
//                .onFailure { error ->
//                    _authState.value = AuthState.Error(error.message ?: "카카오 로그인 실패..")
//                }
//        }
//    }
//}