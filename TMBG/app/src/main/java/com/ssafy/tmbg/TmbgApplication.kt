package com.ssafy.tmbg

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.ssafy.tmbg.util.KeyHashUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TmbgApplication : Application(){
    override fun onCreate() {
        super.onCreate()
//        KeyHashUtil.getKeyHash(this)

        // application 실행 시, 카카오 SDK, 네이버 SDK 초기화
        try {
            KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
            Log.d("SocialLogin", "카카오 SDK 초기화 성공!")
        }catch (e: Exception) {
            Log.e("SocialLogin", "초기화 실패..")
        }
    }
}