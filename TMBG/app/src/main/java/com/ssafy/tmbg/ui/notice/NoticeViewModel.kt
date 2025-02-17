package com.ssafy.tmbg.ui.notice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tmbg.data.notice.repository.NoticeRepositoryImpl
import com.ssafy.tmbg.data.notice.request.PushNoticeRequest
import com.ssafy.tmbg.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NoticeViewModel"

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val noticeRepositoryImpl: NoticeRepositoryImpl
) : ViewModel() {
    private val _state = MutableStateFlow<NoticeState>(NoticeState.Initial)
    val state: StateFlow<NoticeState> = _state.asStateFlow()

    fun fetchNotices(roomId: Long) {
        Log.d(TAG, "fetchNotices 호출: roomId = $roomId")
        viewModelScope.launch {
            _state.value = NoticeState.Loading
            Log.d(TAG, "알림 목록 로딩 시작")

            try {
                Log.d(TAG, "알림 목록 API 호출 시도")
                noticeRepositoryImpl.getNotice(roomId)
                    .onSuccess { notices ->
                        Log.d(TAG, "알림 목록 로드 성공: ${notices.size}개의 알림")
                        _state.value = NoticeState.Success(notices)
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "알림 목록 로드 실패", exception)
                        _state.value = NoticeState.Error(exception.message ?: "알림을 불러오는데 실패했습니다.")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "알림 목록 로드 중 예외 발생", e)
                _state.value = NoticeState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    fun createNotice(roomId: Long, content: String) {
        Log.d(TAG, "createNotice 호출: roomId = $roomId, content = $content")
        viewModelScope.launch {
            try {
                Log.d(TAG, "알림 생성 API 호출 시도")
                noticeRepositoryImpl.createNotice(roomId, content)
                    .onSuccess {
                        Log.d(TAG, "알림 생성 성공, 목록 새로고침")
                        fetchNotices(roomId)
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "알림 생성 실패", exception)
                        _state.value = NoticeState.Error(exception.message ?: "알림 생성에 실패했습니다.")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "알림 생성 중 예외 발생", e)
                _state.value = NoticeState.Error(e.message ?: "알림 생성 중 오류가 발생했습니다.")
            }
        }
    }

    fun createSatisfactionNotice(roomId: Long) {
        Log.d(TAG, "createSatisfactionNotice 호출: roomId = $roomId")
        viewModelScope.launch {
            try {
                Log.d(TAG, "만족도 알림 생성 API 호출 시도")
                noticeRepositoryImpl.createSatisfactionNotice(roomId)
                    .onSuccess {
                        Log.d(TAG, "만족도 알림 생성 성공, 목록 새로고침")
                        fetchNotices(roomId)
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "만족도 알림 생성 실패", exception)
                        _state.value = NoticeState.Error(exception.message ?: "만족도 알림 생성에 실패했습니다.")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "만족도 알림 생성 중 예외 발생", e)
                _state.value = NoticeState.Error(e.message ?: "만족도 알림 생성 중 오류가 발생했습니다.")
            }
        }
    }

    companion object {
        private const val TAG = "NoticeViewModel"
    }
}