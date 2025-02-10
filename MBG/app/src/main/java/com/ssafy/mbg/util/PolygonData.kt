package com.ssafy.mbg.util

import com.google.android.gms.maps.model.LatLng

object PolygonData {
    // 여러 개의 폴리곤 좌표 집합을 저장하는 리스트
    val polygonList: List<List<LatLng>> = listOf(
        listOf(
            LatLng(37.578933180326096, 126.9765742590084), // 좌상
            LatLng(37.578933180326096, 126.97737543646639), // 우상
            LatLng(37.57815848223977, 126.97737543646639), // 우하
            LatLng(37.57815848223977, 126.9765742590084)   // 좌하
        ),
        listOf(
            LatLng(36.10785, 128.4142), // 좌상
            LatLng(36.10829, 128.4217), // 우상
            LatLng(36.10394, 128.4229), // 우하
            LatLng(36.10233, 128.4152)  // 좌하
        )
    )
}
