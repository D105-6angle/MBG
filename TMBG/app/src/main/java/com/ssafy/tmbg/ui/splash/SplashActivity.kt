package com.ssafy.tmbg.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.tmbg.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}