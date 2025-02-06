package com.ssafy.mbg.data.mypage.dto

import com.ssafy.mbg.R

object MyPageDataSource {
    val userInfo = UserInfo(
        "123", "김봉년"
    )
    val solvedProblems = listOf(
        ProblemHistory(
            logId = "1",
            cardId = "c-1",
            imageUrl = R.drawable.cultural_1,
            name = "철퇴",
            lastSolvedAt = "문제 풀이 날짜 : 2024-01-23",
            result = true,
            description = "사찰의 한 물건으로 승려들이 큰소리를 내어 신호를 하거나 " +
                    "불교 의식을 행할 때 사용하던 법구이다. 절에서 스님들이 예불을 드릴 때나 " +
                    "의식을 행할 때도 사용되었다. 낮에는 목어를 치고 밤에는 동종을 치는 것이 " +
                    "일반적이다."
        ),
        ProblemHistory(
            logId = "2",
            cardId = "c-2",
            imageUrl = R.drawable.sajungjeon,
            name = "사정전",
            lastSolvedAt = "문제 풀이 날짜 : 2024-01-20",
            result = true,
            description = "사정전은 조선시대 궁중 건축물로, 임금이 신하들과 회의를 하거나 " +
                    "중요한 정책을 논의하던 장소입니다. 현재는 경복궁에 위치해 있으며, " +
                    "조선의 정치와 문화를 이해하는 데 중요한 유적입니다."
        ),
        ProblemHistory(
            logId = "3",
            cardId = "c-3",
            imageUrl = R.drawable.cultural_2,
            name = "뒤주",
            lastSolvedAt = "문제 풀이 날짜 : 2024-01-19",
            result = false,
            description = "뒤주는 조선시대에 곡식을 보관하던 전통 가구입니다. " +
                    "주로 쌀, 보리 등의 곡물을 보관하는 데 사용되었으며, " +
                    "습기를 막기 위해 바닥을 띄워 제작되었습니다."
        )
    )
}