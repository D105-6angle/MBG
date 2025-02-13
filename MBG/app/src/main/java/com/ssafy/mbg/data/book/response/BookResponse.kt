package com.ssafy.mbg.data.book.response

import com.ssafy.mbg.data.book.dao.CardCollection

data class BookResponse(
    val total_cards: Int,
    val cards: List<CardCollection> = emptyList()
)
