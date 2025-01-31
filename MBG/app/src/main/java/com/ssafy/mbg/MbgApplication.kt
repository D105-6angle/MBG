package com.ssafy.mbg

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MbgApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}