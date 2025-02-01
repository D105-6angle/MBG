package com.ssafy.mbg.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ssafy.mbg.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // ViewBinding로 activity_main.xml inflate
    private lateinit var binding: ActivityMainBinding

    // MVVM: MainViewModel 관리 (Hilt를 통한 주입)
    private val viewModel: MainViewModel by viewModels()

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

        // (옵션) MVVM 방식으로 탭 선택 이벤트 관리
        // 예를 들어, ViewModel의 LiveData를 관찰하여 외부에서 탭 전환을 제어할 수 있음.
        viewModel.selectedTab.observe(this) { selectedItemId ->
            if (binding.bottomNavigation.selectedItemId != selectedItemId) {
                binding.bottomNavigation.selectedItemId = selectedItemId
            }
        }

        // BottomNavigationView의 아이템 선택 리스너를 통해 ViewModel에 이벤트 전달
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            viewModel.selectTab(menuItem.itemId)
            // 기본적인 NavigationUI의 기능으로 목적지 이동
            navController.navigate(menuItem.itemId)
            true
        }
    }

    // 툴바 업 버튼 동작 시 NavController에 위임
    override fun onSupportNavigateUp(): Boolean {
        return findNavController(binding.navHostFragment.id).navigateUp() || super.onSupportNavigateUp()
    }
}
