package com.ssafy.mbg.util

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlin.math.cos
import kotlin.math.sqrt

object PolygonUtils {

    /**
     * 점(point)과 선분(start ~ end) 사이의 최소 거리를 계산합니다.
     * (작은 구간에서는 평면 기하학적 접근을 사용)
     */
    fun distanceToSegment(point: LatLng, start: LatLng, end: LatLng): Double {
        val dStartToPoint = SphericalUtil.computeDistanceBetween(start, point)
        val dStartToEnd = SphericalUtil.computeDistanceBetween(start, end)
        if (dStartToEnd == 0.0) return dStartToPoint

        val headingStartToPoint = SphericalUtil.computeHeading(start, point)
        val headingStartToEnd = SphericalUtil.computeHeading(start, end)
        val theta = Math.toRadians(headingStartToPoint - headingStartToEnd)
        val projection = dStartToPoint * cos(theta)

        return when {
            projection < 0 -> dStartToPoint
            projection > dStartToEnd -> SphericalUtil.computeDistanceBetween(end, point)
            else -> sqrt(dStartToPoint * dStartToPoint - projection * projection)
        }
    }

    /**
     * 주어진 점(point)과 폴리곤(polygon: LatLng 리스트)의 각 선분 사이의 최소 거리를 반환합니다.
     * 만약 점이 폴리곤 내부에 있다면 0에 가까운 값이 반환됩니다.
     */
    fun distanceToPolygon(point: LatLng, polygon: List<LatLng>): Double {
        var minDistance = Double.MAX_VALUE
        for (i in polygon.indices) {
            val start = polygon[i]
            val end = polygon[(i + 1) % polygon.size]
            val distance = distanceToSegment(point, start, end)
            if (distance < minDistance) {
                minDistance = distance
            }
        }
        return minDistance
    }

    /**
     * 선분 위에서 점(point)와 가장 가까운 위치를 계산하여 반환합니다.
     * 사용자와 선분(start ~ end) 사이의 투영 지점을 구합니다.
     */
    fun closestPointOnSegment(point: LatLng, start: LatLng, end: LatLng): LatLng {
        val dStartToPoint = SphericalUtil.computeDistanceBetween(start, point)
        val dStartToEnd = SphericalUtil.computeDistanceBetween(start, end)
        if (dStartToEnd == 0.0) return start

        val headingStartToPoint = SphericalUtil.computeHeading(start, point)
        val headingStartToEnd = SphericalUtil.computeHeading(start, end)
        val theta = Math.toRadians(headingStartToPoint - headingStartToEnd)
        val projection = dStartToPoint * cos(theta)

        return when {
            projection <= 0 -> start
            projection >= dStartToEnd -> end
            else -> {
                val fraction = projection / dStartToEnd
                SphericalUtil.interpolate(start, end, fraction)
            }
        }
    }

    /**
     * 주어진 점(point)과 폴리곤(polygon: LatLng 리스트) 사이의 가장 가까운 점(폴리곤 경계상의 점)을 반환합니다.
     */
    fun closestPointOnPolygon(point: LatLng, polygon: List<LatLng>): LatLng {
        var closestPoint: LatLng? = null
        var minDistance = Double.MAX_VALUE
        for (i in polygon.indices) {
            val start = polygon[i]
            val end = polygon[(i + 1) % polygon.size]
            val candidate = closestPointOnSegment(point, start, end)
            val candidateDistance = SphericalUtil.computeDistanceBetween(point, candidate)
            if (candidateDistance < minDistance) {
                minDistance = candidateDistance
                closestPoint = candidate
            }
        }
        return closestPoint ?: polygon[0]
    }
}
