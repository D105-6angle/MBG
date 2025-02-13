package com.ssafy.mbg.data.book.response

data class BookDetailResponse(
    val cardId : Long,
    val cardName : String,
    val imageUrl : String,
    val collectedAt : String,
    val codeId : String
)
