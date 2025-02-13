package com.ssafy.mbg.data.book.repository

import com.ssafy.mbg.data.book.response.BookDetailResponse
import com.ssafy.mbg.data.book.response.BookResponse

interface BookRepository {
    suspend fun getBook(userId : Long) : Result<BookResponse>
    suspend fun getBookDetail(userId : Long, cardId : Long) : Result<BookDetailResponse>

}