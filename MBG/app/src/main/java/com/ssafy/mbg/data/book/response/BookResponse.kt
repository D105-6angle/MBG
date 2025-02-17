package com.ssafy.mbg.data.book.response

import com.ssafy.mbg.data.book.dao.CardCollection

data class BookResponse(
    val totalCards: Int,
    val totalStoryCards: Int,
    val totalHeritageCards : Int,
    val cards: List<CardCollection> = emptyList()
)
