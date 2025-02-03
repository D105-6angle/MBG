package com.ssafy.tmbg.data.team

import com.ssafy.tmbg.R

object TeamDataSource {
    val dummyTeams = listOf(
        Team(
            id = "1",
            name = "1조",
            number = 1,
            progress = 60,
            members = listOf(
                TeamMember("m1", "김병년"),
                TeamMember("m2", "이용재"),
                TeamMember("m3", "박성민")
            ),
            photos = listOf(
                TeamPhoto("p1", "${R.drawable.group_ex}", "2024-02-03 10:00"),
                TeamPhoto("p2", "${R.drawable.group_ex}", "2024-02-03 11:00")
            ),
            visitedPlaces = listOf(
                TeamPlace("pl1", "수원 화성 공용 화장실", "2024-02-03 09:00"),
                TeamPlace("pl2", "수원 화성 정류장", "2024-02-03 10:30")
            )
        ),
        Team(
            id = "2",
            name = "2조",
            number = 2,
            progress = 45,
            members = listOf(
                TeamMember("m4", "김세현"),
                TeamMember("m5", "심근원")
            ),
            photos = listOf(
                TeamPhoto("p3", "${R.drawable.group_ex}", "2024-02-03 10:30")
            ),
            visitedPlaces = listOf(
                TeamPlace("pl3", "에이 바우트 인동점", "2024-02-03 09:30"),
                TeamPlace("pl4", "대구 광역시 로젠 택배 상하차 센터", "2024-02-03 09:30")

            )
        ),
        Team(
            id = "3",
            name = "3조",
            number = 3,
            progress = 75,
            members = listOf(
                TeamMember("m6", "손은주"),
                TeamMember("m7", "제갈민"),
                TeamMember("m8", "최대규")
            ),
            photos = listOf(
                TeamPhoto("p4", "${R.drawable.group_ex}", "2024-02-03 11:00"),
                TeamPhoto("p5", "${R.drawable.group_ex}", "2024-02-03 11:30")
            ),
            visitedPlaces = listOf(
                TeamPlace("pl5", "투썸 플레이스 인동점", "2024-02-03 10:00"),
                TeamPlace("pl6", "교반 인동점", "2024-02-03 11:00")
            )
        )
    )
}