package com.ssafy.tmbg.ui.splash

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.ssafy.tmbg.R
import com.ssafy.tmbg.databinding.ActivitySplashBinding
import com.ssafy.tmbg.ui.auth.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}