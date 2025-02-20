package com.ssafy.tmbg.data.report

import android.graphics.Color

data class SatisfactionData(
    val type: SatisfactionType,
    val percentage: Float
)
enum class SatisfactionType(val text: String, val color: Int) {
    VERY_GOOD("매우 만족", Color.parseColor("#4A90E2")),    // 파란색
    GOOD("만족", Color.parseColor("#82C8E9")),              // 연한 파란색
    NORMAL("보통", Color.parseColor("#B5E6F8")),           // 아주 연한 파란색
    BAD("불만", Color.parseColor("#FFB6B6")),              // 연한 빨간색
    VERY_BAD("매우 불만", Color.parseColor("#FF8989"))      // 빨간색
}