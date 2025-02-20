package com.ssafy.tmbg.ui

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _roomId = MutableStateFlow<Int>(-1)
    val roomId = _roomId.asStateFlow()

    init {
        _roomId.value = sharedPreferences.getInt(KEY_ROOM_ID, -1)
        Log.d("SharedViewModel", "초기 방 번호: ${_roomId.value}")
    }

    fun setRoomId(id: Int) {
        Log.d("SharedViewModel", "방 번호 설정: $id")
        _roomId.value = id
        sharedPreferences.edit().putInt(KEY_ROOM_ID, id).commit()
    }

    fun clearRoomId() {
        Log.d("SharedViewModel", "방 번호 초기화")
        _roomId.value = -1
        sharedPreferences.edit().remove(KEY_ROOM_ID).commit()
    }

    companion object {
        private const val KEY_ROOM_ID = "room_id"
    }
}