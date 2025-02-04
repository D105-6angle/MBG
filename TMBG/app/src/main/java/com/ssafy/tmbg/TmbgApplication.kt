package com.ssafy.tmbg

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TmbgApplication : Application(){
    override fun onCreate() {
        super.onCreate()
    }
}