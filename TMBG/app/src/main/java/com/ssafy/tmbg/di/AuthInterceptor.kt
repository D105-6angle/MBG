package com.ssafy.tmbg.di

import android.util.Log
import com.ssafy.tmbg.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val serverTokenManager: ServerTokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Log.d("AuthInterceptor", "Original request URL: ${originalRequest.url}")

        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("X-App-type", BuildConfig.APP_TYPE)

        // requiresAuthentication 결과 로깅
        val needsAuth = requiresAuthentication(originalRequest)
        Log.d("AuthInterceptor", "Needs authentication: $needsAuth")

        // 조건문 수정: 인증이 필요한 경우 토큰을 추가
        if (needsAuth) {
            val token = serverTokenManager.getAcessToken()
            Log.d("AuthInterceptor", "Adding token: ${token?.take(10)}...") // 보안을 위해 토큰 일부만 로깅

            if (token.isNullOrEmpty()) {
                Log.e("AuthInterceptor", "Token is null or empty!")
            } else {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }

        val newRequest = requestBuilder.build()

        // 최종 요청 헤더 로깅
        Log.d("AuthInterceptor", "Final request headers: ${newRequest.headers}")

        return try {
            val response = chain.proceed(newRequest)
            Log.d("AuthInterceptor", "Response code: ${response.code}")
            if (!response.isSuccessful) {
                Log.e("AuthInterceptor", "Error response: ${response.message}")
            }
            response
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Network error", e)
            throw e
        }
    }

    private fun requiresAuthentication(request: Request): Boolean {
        val noAuthPath = listOf(
            "/auth/login",
            "/auth/register",
        )
        // 인증이 필요하지 않은 경로에 포함되어 있으면 false, 그렇지 않으면 true 반환
        val result = !noAuthPath.any { path ->
            val matches = request.url.encodedPath.contains(path)
            Log.d("AuthInterceptor", "Checking path ${request.url.encodedPath} against $path: $matches")
            matches
        }
        return result
    }
}