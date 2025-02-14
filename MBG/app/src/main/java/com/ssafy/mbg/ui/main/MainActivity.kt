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
import com.google.firebase.messaging.FirebaseMessaging
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import com.google.firebase.FirebaseApp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.*
import java.io.IOException
import javax.inject.Inject
import com.ssafy.mbg.di.ServerTokenManager
import com.ssafy.mbg.di.UserPreferences
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.content.res.ColorStateList

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

        // 권한 요청 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPostNotificationPermission()
        }

        // NavHostFragment와 NavController 연결
        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
//
//        // route 값 받기
//        val route = intent?.getStringExtra("route")
//        if (route != null && route == "SURVEY") {
//            // FCM 알림에서 전달된 route 값에 따라 PageFragment로 이동
//            navController.navigate(R.id.pageFragment)
//        }

        // route 값 받기
        val route = intent?.getStringExtra("route")
        if (route != null && route == "SURVEY") {
            // FCM 알림에서 전달된 route 값에 따라 PageFragment로 이동
            navController.navigate(R.id.pageFragment)

            // PageFragment로 이동 후, SatisfactionFragment로 자동으로 이동
            navController.navigate(R.id.satisfactionFragment)
        }

        // BottomNavigationView와 NavController 연동
        binding.bottomNavigation.setupWithNavController(navController)

        // FAB 클릭 리스너 설정
        binding.fab.setOnClickListener {
            // 현재 destination이 mapFragment가 아닐 때만 이동
            if (navController.currentDestination?.id != R.id.mapFragment) {
                navController.navigate(R.id.mapFragment)
            }
        }

        // 중앙 메뉴 아이템 완전히 비활성화
        binding.bottomNavigation.menu.getItem(2).apply {
            isEnabled = false
            isVisible = false  // 완전히 숨김
        }

        // BottomNavigationView 클릭 리스너 추가
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> navController.navigate(R.id.homeFragment)
                R.id.taskFragment -> navController.navigate(R.id.taskFragment)
                R.id.bookFragment -> navController.navigate(R.id.bookFragment)
                R.id.pageFragment -> navController.navigate(R.id.pageFragment)
            }
            true
        }

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

        createNotificationChannel("ssafy_channel", "ssafy")
        // FCM 초기화
        initFCM()

        // destination 변경 감지
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mapFragment -> {
                    // 맵 프래그먼트일 때 선택된 상태의 색상
                    binding.fab.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.bottom_bar_fab_background_selected)
                    )
                }
                else -> {
                    // 다른 프래그먼트일 때 선택되지 않은 상태의 색상
                    binding.fab.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.bottom_bar_fab_background_unselected)
                    )
                }
            }
        }
    }

    // 툴바 업 버튼 동작 시 NavController에 위임
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(binding.navHostFragment.id).navigateUp() || super.onSupportNavigateUp()
    }
    // 권한 요청 함수
    private fun requestPostNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        } else {
            // 권한이 이미 허용된 경우
            Log.d("Permission", "Notification permission already granted")
        }
    }

    // 권한 요청 결과 처리 함수
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Notification permission granted")
            } else {
                Log.d("Permission", "Notification permission denied")
            }
        }
    }

    @Inject
    lateinit var userPreferences: UserPreferences
    // FCM 토큰을 받아오는 메서드
    fun initFCM() {
        val userId = userPreferences.userId // userId 가져오기
        Log.d("initFCM", "토큰 요청 시작, userId: $userId")

        try {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Log.d("initFCM", "토큰 성공: $token")
                    // null 체크 후 uploadFcmToken 호출
                    userId?.let { id ->
                        uploadFcmToken(userId = id, fcmToken = token)
                    } ?: Log.e("initFCM", "userId가 null입니다")
                }
                .addOnFailureListener { e ->
                    Log.e("initFCM", "토큰 실패: ${e.message}")
                }
                .addOnCanceledListener {
                    Log.d("initFCM", "토큰 요청 취소됨")
                }
        } catch (e: Exception) {
            Log.e("initFCM", "토큰 요청 예외 발생: ${e.message}")
            e.printStackTrace()
        }
    }
    @Inject
    lateinit var client: OkHttpClient  // Hilt로 주입받은 OkHttpClient 사용

    fun uploadFcmToken(userId: Long, fcmToken: String) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("i12d106.p.ssafy.io")
            .addPathSegment("api")
            .addPathSegment("fcm")
            .addPathSegment("add")
            .addQueryParameter("userId", userId.toString())
            .addQueryParameter("fcmToken", fcmToken)
            .build()

        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM", "토큰 업로드 실패", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        Log.d("FCM", "토큰 업로드 성공: ${response.body?.string()}")
                    } else {
                        Log.e("FCM", "토큰 업로드 실패: ${response.code} - ${response.message}")
                    }
                }
            }
        })
    }

    private fun createNotificationChannel(id: String, name: String) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance)

        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
