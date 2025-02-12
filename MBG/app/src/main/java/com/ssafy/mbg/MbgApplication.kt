package com.ssafy.mbg

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import com.ssafy.mbg.util.KeyHashUtil
import dagger.hilt.android.HiltAndroidApp
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

@HiltAndroidApp
class MbgApplication : Application() {
    private val SocialTAG = "SocialLogin"
    override fun onCreate() {
        super.onCreate()

        try {
            KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
            Log.d(SocialTAG, "카카오 SDK 초기화 성공")

            NaverIdLoginSDK.initialize(
                context = this,
                clientId = BuildConfig.NAVER_CLIENT_ID,
                clientSecret = BuildConfig.NAVER_CLIENT_SECRET,
                clientName = getString(R.string.app_name)
            )
            Log.d(SocialTAG, "네이버 SDK 초기화 성공")
        } catch (e: Exception) {
            Log.d(SocialTAG, "초기화 실패")
        }
        val key = KeyHashUtil.getKeyHash(this)
        Log.d(SocialTAG, key)

        FirebaseApp.initializeApp(this)

    }
}