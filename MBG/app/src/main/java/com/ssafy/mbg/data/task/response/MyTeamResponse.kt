package com.ssafy.mbg.data.task.response

import com.ssafy.mbg.data.task.dao.Member
import com.ssafy.mbg.data.task.dao.VerificationPhotos
import com.ssafy.mbg.data.task.dao.VisitedPlace

data class MyTeamResponse(
    val groupNo : Long,
    val progress : Double,
    val members : List<Member>,
    val verificationPhotos : List<VerificationPhotos>,
    val vistiedPlaces : List<VisitedPlace>,

)
