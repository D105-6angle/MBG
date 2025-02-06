package com.ssafy.tmbg.data.auth.repository

import android.content.Context
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.ssafy.tmbg.data.auth.dto.SocialLoginResult
import com.ssafy.tmbg.data.auth.dto.SocialToken
import com.ssafy.tmbg.data.auth.dto.SocialUserInfo
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class KakaoLoginRepositoryImpl @Inject constructor(
    private val context: Context
) : SocialLoginRepository {
    override suspend fun login(): Result<SocialLoginResult> = suspendCoroutine { continuation ->
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk(continuation)
        } else {
            loginWithKakaoAccount(continuation)
        }
    }

    private fun getUserInfo(continuation: Continuation<Result<SocialLoginResult>>) {
        // 사용자 정보 가져오기
        UserApiClient.instance.me { user, error ->
            when {
                error != null -> continuation.resume(Result.failure(error))
                user != null -> {
                    // 토큰 정보 가져오기
                    UserApiClient.instance.accessTokenInfo { tokenInfo, tokenError ->
                        when {
                            tokenError != null -> continuation.resume(Result.failure(tokenError))
                            tokenInfo != null -> {
                                // TokenManager를 통해 토큰 얻기
                                val token = AuthApiClient.instance.tokenManagerProvider.manager.getToken()

                                val socialToken = SocialToken(
                                    accessToken = token?.accessToken ?: "",
                                    refreshToken = token?.refreshToken ?: "",
                                    tokenExpiresAt = tokenInfo.expiresIn.toString()
                                )

                                val socialLoginResult = SocialLoginResult(
                                    userInfo = SocialUserInfo(
                                        providerId = user.id.toString(),
                                        email = user.kakaoAccount?.email ?: "",
                                        name = user.kakaoAccount?.profile?.nickname ?: ""
                                    ),
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

    private fun loginWithKakaoAccount(continuation: Continuation<Result<SocialLoginResult>>) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            when {
                error != null -> continuation.resume(Result.failure(error))
                token != null -> getUserInfo(continuation)
            }
        }
    }
}