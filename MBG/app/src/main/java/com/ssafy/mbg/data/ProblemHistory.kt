package com.ssafy.mbg.data

data class ProblemHistory(
    val iconResId : Int,
    val title : String,
    val lastSolvedAt : String,
    val result : Boolean,
    val imageResId: Int? = null,  // 상세 페이지용 큰 이미지
    val description: String? = null  // 문화재 설명
)
