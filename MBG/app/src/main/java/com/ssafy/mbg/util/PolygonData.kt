package com.ssafy.mbg.util

import com.google.android.gms.maps.model.LatLng

object PolygonData {
    // 여러 개의 폴리곤 좌표 집합을 저장하는 리스트
    val polygonList: List<List<LatLng>> = listOf(
        listOf(
            LatLng(37.579984, 126.975801), // 좌상
            LatLng(37.579955, 126.976384), // 우상
            LatLng(37.579525, 126.976356),  // 우하
            LatLng(37.579554, 126.975773)    // 좌하
        ),
        listOf(
            LatLng(37.578773, 126.976712), // 좌상
            LatLng(37.578748, 126.977304), // 우상
            LatLng(37.578246, 126.977276), // 우하
            LatLng(37.578266, 126.976673)  // 좌하
        ),
        listOf(
            LatLng(37.579196, 126.976871), // 좌상
            LatLng(37.579174, 126.977202), // 우상
            LatLng(37.578982, 126.977191), // 우하
            LatLng(37.578996, 126.976848)  // 좌하
        )

    )
}
