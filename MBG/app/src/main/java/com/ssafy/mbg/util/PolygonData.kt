package com.ssafy.mbg.util

import com.google.android.gms.maps.model.LatLng

object PolygonData {
    val polygonCoordinates: List<LatLng> = listOf(
        LatLng(36.10785, 128.4142), // 좌상
        LatLng(36.10829, 128.4217), // 우상
        LatLng(36.10394, 128.4229), // 우하
        LatLng(36.10233, 128.4152)  // 좌하
        // 필요에 따라 좌표를 추가하면 됩니다.
    )
}
