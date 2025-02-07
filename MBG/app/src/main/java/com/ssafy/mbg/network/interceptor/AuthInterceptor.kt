package com.ssafy.mbg.network.interceptor


import com.ssafy.mbg.BuildConfig
import com.ssafy.mbg.data.manger.ServerTokenManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

/**
 * API 요청에 인증 정보를 포함하기 위한 인터셉터
 */
class AuthInterceptor @Inject constructor(
    private val serverTokenManager: ServerTokenManager
) : Interceptor {
    /**
     * HTTP 요청을 가로채서 헤더에 인증 정보를 추가
     *
     * @param chain Interceptor.Chain 현재 요청에 대한 정보를 담고 있는 체인
     * @return Response 인증 정보가 포함된 새로운 요청에 대한 응답
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        /**
         * 현재 요청에서 인증이 필요 없다면,
         * Content-type만 추가 해줌
         */
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("X-App-type",BuildConfig.APP_TYPE)

        /**
         * 현재 요청 에서 인증이 필요하다면,
         * header에 token 값 추가
         */
        if(!requiresAuthentication(originalRequest)) {
            val token = serverTokenManager.getAcessToken()
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }


        return chain.proceed(requestBuilder.build())

    }

    /**
     * 인증이 필요하지 않은 경우 필터링
     * 1. 로그인
     * 2. 회원 가입
     * 이 두 경우, 토큰 값을 통해 인증을 확인 하지 않으므로
     */
    private fun requiresAuthentication(request : Request) : Boolean {
        val noAuthPath = listOf(
            "/auth/login",
            "/auth/register",
            "/rooms/{roomId}/schedules"
        )
        return !noAuthPath.any { request.url.encodedPath.contains(it)}
    }

}