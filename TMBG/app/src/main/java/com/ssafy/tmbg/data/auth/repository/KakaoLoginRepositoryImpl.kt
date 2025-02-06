package com.ssafy.tmbg.data.auth.repository

import android.content.Context
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.ssafy.tmbg.data.auth.dao.SocialUserInfo
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
    override suspend fun login(): Result<SocialUserInfo> = suspendCoroutine{ continuation ->
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
    private fun loginWithKakaoTalk(continuation: Continuation<Result<SocialUserInfo>>) {
        UserApiClient.instance.loginWithKakaoTalk(context) {token, error ->
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
    private fun loginWithKakaoAccount(continuation: Continuation<Result<SocialUserInfo>>) {
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
    private fun getUserInfo(continuation: Continuation<Result<SocialUserInfo>>) {
        UserApiClient.instance.me { user, error ->
            when {
                error != null -> continuation.resume(Result.failure(error))
                user != null -> {
                    val userInfo = SocialUserInfo(
                        providerId = user.id.toString(),
                        email = user.kakaoAccount?.email ?: "",
                        name = user.kakaoAccount?.profile?.nickname ?: ""
                    )
                    continuation.resume(Result.success(userInfo))
                }
            }
        }
    }
}