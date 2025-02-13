//package com.ssafy.mbg.util
//
//import com.google.android.gms.maps.model.LatLng
//
//object PolygonData {
//    // Nest the Mission class here so that it can be accessed as PolygonData.Mission
//    data class Mission(
//        val missionId: Int,
//        val positionName: String?,
//        val codeId: String,
//        val centerPoint: LatLng,
//        val edgePoints: List<LatLng>,
//        val correct: Boolean
//    )
//
//    val missionList: List<Mission> = listOf(
//        Mission(
//            missionId = 1,
//            positionName = "경회루",
//            codeId = "M001",
//            centerPoint = LatLng(37.579768, 126.975996),
//            edgePoints = listOf(
//                LatLng(37.579984, 126.975801),
//                LatLng(37.579955, 126.976384),
//                LatLng(37.579525, 126.976356),
//                LatLng(37.579554, 126.975773)
//            ),
//            correct = false
//        ),
//        Mission(
//            missionId = 2,
//            positionName = "근정전",
//            codeId = "M001",
//            centerPoint = LatLng(37.578562, 126.976997),
//            edgePoints = listOf(
//                LatLng(37.578773, 126.976712),
//                LatLng(37.578748, 126.977304),
//                LatLng(37.578246, 126.977276),
//                LatLng(37.578266, 126.976673)
//            ),
//            correct = false
//        ),
//        Mission(
//            missionId = 3,
//            positionName = "사정전",
//            codeId = "M001",
//            centerPoint = LatLng(37.579106, 126.977041),
//            edgePoints = listOf(
//                LatLng(37.579203, 126.976893),
//                LatLng(37.579203, 126.977205),
//                LatLng(37.578991, 126.977194),
//                LatLng(37.579012, 126.976865)
//            ),
//            correct = false
//        ),
//        Mission(
//            missionId = 5,
//            positionName = "랜덤 미션",
//            codeId = "M002",
//            centerPoint = LatLng(37.577551, 126.976469),
//            edgePoints = listOf(
//                LatLng(37.577626, 126.976416),
//                LatLng(37.577626, 126.97654),
//                LatLng(37.577496, 126.976537),
//                LatLng(37.5775, 126.97641)
//            ),
//            correct = false
//        ),
//        // 새롭게 추가된 미션들
//        Mission(
//            missionId = 8,
//            positionName = "자경전",
//            codeId = "M003",
//            centerPoint = LatLng(37.580314, 126.978127),
//            edgePoints = listOf(
//                LatLng(37.580595, 126.977787),
//                LatLng(37.580555, 126.978552),
//                LatLng(37.579983, 126.978557),
//                LatLng(37.58001, 126.977751)
//            ),
//            correct = false
//        ),
//        Mission(
//            missionId = 9,
//            positionName = "향원정",
//            codeId = "M003",
//            centerPoint = LatLng(37.58237, 126.977052),
//            edgePoints = listOf(
//                LatLng(37.582843, 126.976867),
//                LatLng(37.582848, 126.97724),
//                LatLng(37.582233, 126.977227),
//                LatLng(37.582226, 126.97685)
//            ),
//            correct = false
//        )
//    )
//}
