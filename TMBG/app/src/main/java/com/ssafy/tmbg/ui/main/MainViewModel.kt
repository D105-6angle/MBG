package com.ssafy.tmbg.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
        private val _roomId = MutableLiveData<Long>()
        val roomId: LiveData<Long> = _roomId

        fun setRoomId(id: Long) {
            _roomId.value = id
        }
}
