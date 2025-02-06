package com.ssafy.tmbg.data.auth.repository

import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.ssafy.tmbg.data.auth.dto.SocialUserInfo
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 네이버 로그인 Repository 구현체
 * 네이버 SDK를 사용하여 사용자 정보를 가져오는 기능을 구현
 */
class NaverLoginRepositoryImpl @Inject constructor() : SocialLoginRepository {
    /**
     * 네이버 로그인을 통해 사용자 정보를 가져오는 suspend 함수
     * @return Result<SocialUserInfo> 성공 시 사용자 정보, 실패 시 에러 메시지를 포함한 Result 객체
     */
    override suspend fun login(): Result<SocialUserInfo> = suspendCoroutine { continuation ->
        // 네이버 SDK에서 액세스 토큰 가져오기
        val accessToken = NaverIdLoginSDK.getAccessToken()

        // 액세스 토큰이 없는 경우 로그인 필요 에러 반환
        if (accessToken.isNullOrEmpty()) {
            continuation.resume(Result.failure(Exception("네이버 로그인이 필요합니다")))
            return@suspendCoroutine
        }

        // 액세스 토큰이 있는 경우 사용자 정보 요청
        getUserInfo(continuation)
    }

    /**
     * 네이버 SDK를 사용하여 사용자 프로필 정보를 요청하는 private 함수
     * @param continuation Coroutine continuation 객체로 비동기 결과를 전달
     */
    private fun getUserInfo(continuation: Continuation<Result<SocialUserInfo>>) {
        NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
            /**
             * 프로필 정보 요청 성공 시 호출
             * @param result 네이버에서 반환한 사용자 프로필 정보
             */
            override fun onSuccess(result: NidProfileResponse) {
                val userInfo = SocialUserInfo(
                    providerId = result.profile?.id ?: "",
                    email = result.profile?.email ?: "",
                    name = result.profile?.name ?: ""
                )
                continuation.resume(Result.success(userInfo))
            }

            /**
             * HTTP 통신 실패 시 호출
             * @param httpStatus HTTP 상태 코드
             * @param message 에러 메시지
             */
            override fun onFailure(httpStatus: Int, message: String) {
                val error = when (httpStatus) {
                    401 -> "인증에 실패했습니다"
                    404 -> "사용자 정보를 찾을 수 없습니다"
                    else -> "네트워크 오류가 발생했습니다 (Error: $httpStatus)"
                }
                continuation.resume(Result.failure(Exception(error)))
            }

            /**
             * SDK 내부 에러 발생 시 호출
             * @param errorCode 에러 코드
             * @param message 에러 메시지
             */
            override fun onError(errorCode: Int, message: String) {
                val error = when (errorCode) {
                    -1 -> "네이버 로그인이 취소되었습니다"
                    else -> "로그인 중 오류가 발생했습니다 (Error: $errorCode)"
                }
                continuation.resume(Result.failure(Exception(error)))
            }
        })
    }
}