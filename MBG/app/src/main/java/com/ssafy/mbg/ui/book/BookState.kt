package com.ssafy.mbg.ui.book

import com.ssafy.mbg.data.book.response.BookResponse

sealed class BookState {
    data object Initial : BookState()
    data object Loading : BookState()
    data class Success(val bookResponse: BookResponse) : BookState()  // non-null로 변경
    data class Error(val message: String) : BookState()
}