package com.ssafy.tmbg

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import com.ssafy.tmbg.util.KeyHashUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TmbgApplication : Application(){
    override fun onCreate() {
        super.onCreate()
//        KeyHashUtil.getKeyHash(this)
        KeyHashUtil.getReleaseKeyHash(this)

        // application 실행 시, 카카오 SDK, 네이버 SDK 초기화
        try {
            // 카카오 SDK
            KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
            Log.d("SocialLogin", "카카오 SDK 초기화 성공!")

            // 네이버 SDK
            NaverIdLoginSDK.initialize(
                context = this,
                clientId = BuildConfig.NAVER_CLIENT_ID,
                clientSecret = BuildConfig.NAVER_CLIENT_SECRET,
                clientName = getString(R.string.app_name)
            )
            Log.d("SocialLogin", "네이버 SDK 초기화 성공!")

            val key = KeyHashUtil.getKeyHash(this)
            Log.d("KeyHash", key)
        }catch (e: Exception) {
            Log.e("SocialLogin", "초기화 실패..")
        }
    }
}