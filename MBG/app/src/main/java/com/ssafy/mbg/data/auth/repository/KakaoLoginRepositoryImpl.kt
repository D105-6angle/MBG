package com.ssafy.mbg.data.auth.repository

import android.content.Context
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.ssafy.mbg.data.auth.dto.SocialLoginResult
import com.ssafy.mbg.data.auth.dto.SocialUserInfo
import com.ssafy.mbg.data.auth.dto.TokenResponse
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class KakaoLoginRepositoryImpl @Inject constructor(
    private val context: Context
) : SocialLoginRepository {
    /**
     * 카카오 로그인 수행
     * 카카오톡 앱이 있으면 앱으로, 없으면 계정으로 로그인 시도
     */
    override suspend fun login(): Result<SocialLoginResult> = suspendCoroutine { continuation ->
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk(continuation)
        } else {
            loginWithKakaoAccount(continuation)
        }
    }

    /**
     * 카카오톡 앱을 통한 로그인 처리
     * 실패 시 카카오 계정으로 로그인 시도
     */
    private fun loginWithKakaoTalk(continuation: Continuation<Result<SocialLoginResult>>) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            when {
                error != null -> {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        continuation.resume(Result.failure(Exception("로그인이 취소 됐어요!")))
                        return@loginWithKakaoTalk
                    }

                    loginWithKakaoAccount(continuation)
                }

                token != null -> getUserInfo(continuation)
            }
        }
    }

    /** 카카오 계정을 통한 로그인 처리 */
    private fun loginWithKakaoAccount(continuation: Continuation<Result<SocialLoginResult>>) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            when {
                error != null -> continuation.resume(Result.failure(error))
                token != null -> getUserInfo(continuation)
            }
        }
    }

    /**
     * 로그인 성공 후 사용자 정보 요청
     * providerId: 카카오 회원번호
     * email: 카카오계정 이메일
     * name: 카카오 닉네임
     */
    private fun getUserInfo(continuation: Continuation<Result<SocialLoginResult>>) {
        UserApiClient.instance.me { user, error ->
            when {
                error != null -> continuation.resume(Result.failure(error))
                user != null -> {
                    UserApiClient.instance.accessTokenInfo { tokenInfo, tokenError ->
                        when {
                            tokenError != null -> continuation.resume(Result.failure(tokenError))
                            tokenInfo != null -> {
                                val token = AuthApiClient.instance.tokenManagerProvider.manager.getToken()

                                val socialToken = TokenResponse(
                                    accessToken = token?.accessToken ?: "",
                                    refreshToken = token?.refreshToken ?: "",
                                    tokenExpiresAt = tokenInfo.expiresIn.toString()
                                )

                                val socialUserInfo = SocialUserInfo(
                                    providerId = user.id.toString(),
                                    email = user.kakaoAccount?.email ?: "",
                                    name = user.kakaoAccount?.profile?.nickname ?: ""
                                )

                                val socialLoginResult = SocialLoginResult(
                                    socialUserInfo = socialUserInfo,
                                    token = socialToken
                                )
                                continuation.resume(Result.success(socialLoginResult))
                            }
                        }
                    }
                }
            }
        }
    }
}