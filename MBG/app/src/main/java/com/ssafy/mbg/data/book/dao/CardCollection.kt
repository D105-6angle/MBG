package com.ssafy.mbg.data.book.dao

data class CardCollection(
    val cardId : Long,
    val cardName : String,
    val imageUrl : String,
    val collectedAt : String,
    val codeId : String
)