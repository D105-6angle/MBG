package com.ssafy.mbg.api

import com.ssafy.mbg.data.book.response.BookDetailResponse
import com.ssafy.mbg.data.book.response.BookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface BookApi {
    @GET("users/{userId}/heritagebook")
    suspend fun getBook(
        @Path("userId") userId : Long,
    ) : Response<BookResponse>

    @GET("users/{userId}/heritagebook/cards/{cardId}")
    suspend fun getBookDetail(
        @Path("userId") userId: Long,
        @Path("cardId") cardId : Long
    ) : Response<BookDetailResponse>
}