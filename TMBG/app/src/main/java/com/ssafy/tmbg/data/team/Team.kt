package com.ssafy.tmbg.data.team

// Team 데이터
data class Team(
    val id: String,
    val name : String,
    val number : Int,
    val progress : Int,
    val members: List<TeamMember>,
    val photos : List<TeamPhoto>,
    val visitedPlaces : List<TeamPlace>
)
