package com.ssafy.mbg.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    // 선택된 탭의 메뉴 아이템 id를 관리하는 LiveData
    private val _selectedTab = MutableLiveData<Int>()
    val selectedTab: LiveData<Int> get() = _selectedTab

    // 탭 선택 이벤트 처리
    fun selectTab(itemId: Int) {
        _selectedTab.value = itemId
    }
}
