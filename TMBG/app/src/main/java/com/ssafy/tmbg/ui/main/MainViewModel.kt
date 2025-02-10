package com.ssafy.tmbg.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _roomId = MutableLiveData<Int>()
    val roomId: LiveData<Int> = _roomId

    companion object {
        private const val KEY_ROOM_ID = "room_id"
    }

    init {
        // SharedPreferences에서 저장된 roomId를 불러옴 (없으면 -1)
        _roomId.value = sharedPreferences.getInt(KEY_ROOM_ID, -1)
    }

    fun setRoomId(id: Int) {
        _roomId.value = id
        // SharedPreferences에 roomId 저장
        sharedPreferences.edit().putInt(KEY_ROOM_ID, id).apply()
    }

    fun clearRoomId() {
        _roomId.value = -1
        // SharedPreferences에서 roomId 제거
        sharedPreferences.edit().remove(KEY_ROOM_ID).apply()
    }
}
