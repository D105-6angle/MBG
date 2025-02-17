package com.ssafy.tmbg.ui.notice

import com.ssafy.tmbg.data.notice.response.NoticeResponse

sealed class NoticeState {
    data object Initial : NoticeState()
    data object Loading : NoticeState()
    data class Success(val notices : List<NoticeResponse>) : NoticeState()
    data class Error(val message : String) : NoticeState()
}