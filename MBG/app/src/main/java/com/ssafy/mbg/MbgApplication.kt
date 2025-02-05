package com.ssafy.mbg

import android.app.Application
import android.util.Log
import com.ssafy.mbg.util.KeyHashUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MbgApplication : Application() {
//    private val TAG = "MBG"
    override fun onCreate() {
        super.onCreate()
//        val key = KeyHashUtil.getKeyHash(this)
//        Log.d(TAG, key)
    }
}