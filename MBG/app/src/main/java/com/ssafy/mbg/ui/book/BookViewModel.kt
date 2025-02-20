package com.ssafy.mbg.ui.book

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.mbg.data.book.repository.BookRepository
import com.ssafy.mbg.data.book.response.BookResponse
import com.ssafy.mbg.di.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BookViewModel"

@HiltViewModel
class BookViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val repository: BookRepository
) : ViewModel() {

    private val _bookState = MutableStateFlow<BookState>(BookState.Initial)
    val bookState = _bookState.asStateFlow()

    fun getUserId() = userPreferences.userId

    fun getBook(userId: Long) {
        viewModelScope.launch {
            _bookState.value = BookState.Loading

            try {
                repository.getBook(userId).fold(
                    onSuccess = { response ->
                        _bookState.value = BookState.Success(response)
                        Log.d(TAG, "도감 조회 성공: $response")
                    },
                    onFailure = { exception ->
                        _bookState.value = BookState.Error(exception.message ?: "Unknown error")
                        Log.e(TAG, "도감 조회 실패", exception)
                    }
                )
            } catch (e: Exception) {
                _bookState.value = BookState.Error(e.message ?: "Unknown error")
                Log.e(TAG, "도감 조회 중 예외 발생", e)
            }
        }
    }

//    fun getBookDetail(userId: Long, cardId: Long) {
//        viewModelScope.launch {
//            _bookDetailState.value = BookDetailState.Loading
//
//            try {
//                repository.getBookDetail(userId, cardId).fold(
//                    onSuccess = { response ->
//                        _bookDetailState.value = BookDetailState.Success(response)
//                        Log.d(TAG, "도감 상세 조회 성공: $response")
//                    },
//                    onFailure = { exception ->
//                        _bookDetailState.value = BookDetailState.Error(exception.message ?: "Unknown error")
//                        Log.e(TAG, "도감 상세 조회 실패", exception)
//                    }
//                )
//            } catch (e: Exception) {
//                _bookDetailState.value = BookDetailState.Error(e.message ?: "Unknown error")
//                Log.e(TAG, "도감 상세 조회 중 예외 발생", e)
//            }
//        }
//    }
}