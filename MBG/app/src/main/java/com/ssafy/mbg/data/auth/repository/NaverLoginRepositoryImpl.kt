package com.ssafy.mbg.data.auth.repository

import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.ssafy.mbg.data.auth.dao.SocialUserInfo
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NaverLoginRepositoryImpl @Inject constructor() : SocialLoginRepository {

    override suspend fun login(): Result<SocialUserInfo> = suspendCoroutine { continuation ->
        val accessToken = NaverIdLoginSDK.getAccessToken()

        if (accessToken.isNullOrEmpty()) {
            continuation.resume(Result.failure(Exception("네이버 로그인이 필요합니다")))
            return@suspendCoroutine
        }


        getUserInfo(continuation)
    }

    private fun getUserInfo(continuation: Continuation<Result<SocialUserInfo>>) {
        NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                val socialUserInfo = SocialUserInfo(
                        providerId = result.profile?.id ?: "",
                        email = result.profile?.email ?: "",
                        name = result.profile?.name ?: ""
                    )


                continuation.resume(Result.success(socialUserInfo))
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val error = when (httpStatus) {
                    401 -> "인증에 실패했습니다"
                    404 -> "사용자 정보를 찾을 수 없습니다"
                    else -> "네트워크 오류가 발생했습니다"
                }
                continuation.resume(Result.failure(Exception(error)))
            }

            override fun onError(errorCode: Int, message: String) {
                val error = when (errorCode) {
                    -1 -> "네이버 로그인이 취소 되었습니다."
                    else -> "로그인 중 오류가 발생했습니다."
                }
                continuation.resume(Result.failure(Exception(error)))
            }
        })
    }
}