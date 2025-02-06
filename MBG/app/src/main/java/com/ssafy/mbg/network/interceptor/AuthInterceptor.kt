package com.ssafy.mbg.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * API 요청에 인증 정보를 포함하기 위한 인터셉터
 */
class AuthInterceptor : Interceptor {
    /**
     * HTTP 요청을 가로채서 헤더에 인증 정보를 추가
     *
     * @param chain Interceptor.Chain 현재 요청에 대한 정보를 담고 있는 체인
     * @return Response 인증 정보가 포함된 새로운 요청에 대한 응답
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            // Content-Type 헤더 추가
            .addHeader("Content-Type", "application/json")
            // Authorization 헤더에 Bearer 토큰 추가
            .addHeader("Authorization", "Bearer ${getToken()}")
            .build()
        return chain.proceed(request)
    }

    /**
     * 인증 토큰을 가져오는 메서드
     *
     * @return String 인증 토큰
     * TODO: 서버 로직 구현 후 실제 토큰을 반환하도록 수정 필요
     */
    private fun getToken() : String {
        // 서버에서 로직 구현 후 추가할게용
        return ""
    }
}