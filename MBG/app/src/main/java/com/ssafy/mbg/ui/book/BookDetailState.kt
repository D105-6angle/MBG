package com.ssafy.mbg.ui.book

import com.ssafy.mbg.data.book.response.BookDetailResponse

sealed class BookDetailState {
    data object Initial : BookDetailState()
    data object Loading : BookDetailState()
    data class Success(val data: BookDetailResponse) : BookDetailState()
    data class Error(val message: String) : BookDetailState()

}
