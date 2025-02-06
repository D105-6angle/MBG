package com.ssafy.mbg.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 뒤로가기 두 번 클릭 시 종료를 위한 변수
    private var backPressedTime = 0L
    private val backPressInterval = 2000L  // 2초

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NavHostFragment와 NavController 연결
        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController

        // BottomNavigationView와 NavController 연동
        binding.bottomNavigation.setupWithNavController(navController)

        // 뒤로가기로 앱 종료를 제어할 탑 레벨(=탭) 목적지들
        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.taskFragment,
            R.id.mapFragment,
            R.id.bookFragment,
            R.id.pageFragment
        )

        // OnBackPressedDispatcher + OnBackPressedCallback 등록
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 현재 최상단(가장 최근) Destination 확인
                val currentDestinationId = navController.currentDestination?.id

                // 탭 화면(Top-level Destination)인지 여부
                if (currentDestinationId != null && currentDestinationId in topLevelDestinations) {
                    // 탭 화면이면 -> "두 번 누르면 종료" 로직
                    if (System.currentTimeMillis() - backPressedTime < backPressInterval) {
                        finish() // Activity 종료
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "한번 더 누르면 종료됩니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        backPressedTime = System.currentTimeMillis()
                    }
                } else {
                    // 탭 화면이 아니라면(popBackStack()이 가능하다면) -> 뒤로가기
                    if (!navController.popBackStack()) {
                        // popBackStack() 실패할 일은 거의 없지만, 혹시 모르니 동일 로직
                        if (System.currentTimeMillis() - backPressedTime < backPressInterval) {
                            finish()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "한번 더 누르면 종료됩니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            backPressedTime = System.currentTimeMillis()
                        }
                    }
                }
            }
        })
    }

    // 툴바 업 버튼 동작 시 NavController에 위임
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(binding.navHostFragment.id).navigateUp() || super.onSupportNavigateUp()
    }
}
