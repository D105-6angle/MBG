package com.ssafy.mbg.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.google.gson.Gson
import com.ssafy.mbg.R
import com.ssafy.mbg.di.UserPreferences
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // 사용자 위치 마커
    private var userMarker: Marker? = null
    // QuizFragment를 한 번만 보여주기 위한 플래그
    private var isQuizFragmentShown = false
    // 최초 위치 파악 후 카메라 이동 여부
    private var isUserLocationUpdated = false

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    // API를 통해 받아온 미션 데이터 (초기값 empty)
    private var missionList: List<Mission> = emptyList()
    // 미션의 폴리곤들을 저장할 리스트
    private val drawnPolygons = mutableListOf<Polygon>()

    // Bottom Sheet 내 텍스트뷰 (피커 리스트와 거리 표시)
    private lateinit var bottomSheetTextView: AppCompatTextView

    // 데이터 클래스: Picker (이름과 좌표)
    data class Picker(val name: String, val location: LatLng)

    // 미션 정보를 Picker로 변환하는 함수
    private fun getMissionPickers(): List<Picker> {
        return missionList.map { mission -> Picker(mission.positionName ?: "미지정", mission.getCenterPointLatLng()) }
    }

    // 추가된 피커 리스트 (동적으로 추가됨)
    private val additionalPickerList = mutableListOf<Picker>()
    // 추가된 피커 마커와 원을 제거하기 위한 리스트
    private val additionalPickerMarkers = mutableListOf<Marker>()
    private val additionalPickerCircles = mutableListOf<Circle>()

    // 위치 선택 모드 플래그 (지도 클릭 시 추가 피커 등록)
    private var isPickMode = false
    // 추가 피커 번호 카운터 (자동 이름 부여용)
    private var additionalPickerCount = 1

    // QuizFragment에서 사용할 피커와의 반경 (100m)
    private val quizRadiusInMeters = 100.0
    // 추가된 피커에 표시할 반경 (70m, 노란색)
    private val additionalPickerRadiusInMeters = 70.0

    // 가장 가까운 대상으로 사용자 위치와 연결할 선(Polyline)
    private var nearestLine: Polyline? = null

    // 모드 토글: Picker Mode vs. Auto Mode (기본은 Picker Mode)
    private var isPickerModeEnabled = true

    // Picker Mode의 고정 초기 위치 (경복궁)
    private val INITIAL_PICKER_LATLNG = LatLng(37.57640594972532, 126.97686654390287)

    // API 응답 JSON과 매핑되는 미션 데이터 모델 (centerPoint와 edgePoints는 [lat, lng] 배열)
    data class Mission(
        val missionId: Int,
        val positionName: String?,
        val codeId: String,
        val centerPoint: List<Double>,
        val edgePoints: List<List<Double>>,
        val correct: Boolean
    ) {
        fun getCenterPointLatLng(): LatLng = LatLng(centerPoint[0], centerPoint[1])
        fun getEdgePointsLatLng(): List<LatLng> = edgePoints.map { LatLng(it[0], it[1]) }
    }

    @Inject
    lateinit var client: okhttp3.OkHttpClient

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_map.xml에는 지도, Pick Location 버튼, 토글 버튼, 화살표(GridLayout) 버튼, Bottom Sheet가 포함됨
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        bottomSheetTextView = view.findViewById(R.id.bottomSheetTitleTextView)

        // 지도 Fragment 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // Picker Mode가 기본이므로 토글 버튼을 기본 체크 상태로 설정
        val toggleMode: ToggleButton = view.findViewById(R.id.toggle_mode)
        toggleMode.isChecked = true  // 기본: Picker Mode
        // arrow_container를 GridLayout으로 참조 (XML에 정의된 GridLayout)
        val arrowContainer: GridLayout = view.findViewById(R.id.arrow_container)
        arrowContainer.visibility = View.VISIBLE

        toggleMode.setOnCheckedChangeListener { _, isChecked ->
            isPickerModeEnabled = isChecked
            if (isPickerModeEnabled) {
                arrowContainer.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Picker Mode 활성화", Toast.LENGTH_SHORT).show()
            } else {
                arrowContainer.visibility = View.GONE
                Toast.makeText(requireContext(), "Auto Mode 활성화", Toast.LENGTH_SHORT).show()
            }
        }

        // 화살표 버튼 리스너 (Picker Mode에서 내 위치 피커를 직접 이동)
        val btnArrowUp: ImageButton = view.findViewById(R.id.btn_arrow_up)
        val btnArrowDown: ImageButton = view.findViewById(R.id.btn_arrow_down)
        val btnArrowLeft: ImageButton = view.findViewById(R.id.btn_arrow_left)
        val btnArrowRight: ImageButton = view.findViewById(R.id.btn_arrow_right)
        val moveDistance = 10.0  // 10미터씩 이동

        btnArrowUp.setOnClickListener {
            userMarker?.let {
                val newPos = SphericalUtil.computeOffset(it.position, moveDistance, 0.0)
                it.position = newPos
                updateDistanceDisplay(newPos)
                drawLineToNearestTarget(newPos)
                checkIfInsidePolygon(newPos) // 추가: picker mode 위치도 체크
            }
        }
        btnArrowDown.setOnClickListener {
            userMarker?.let {
                val newPos = SphericalUtil.computeOffset(it.position, moveDistance, 180.0)
                it.position = newPos
                updateDistanceDisplay(newPos)
                drawLineToNearestTarget(newPos)
            }
        }
        btnArrowLeft.setOnClickListener {
            userMarker?.let {
                val newPos = SphericalUtil.computeOffset(it.position, moveDistance, 270.0)
                it.position = newPos
                updateDistanceDisplay(newPos)
                drawLineToNearestTarget(newPos)
            }
        }
        btnArrowRight.setOnClickListener {
            userMarker?.let {
                val newPos = SphericalUtil.computeOffset(it.position, moveDistance, 90.0)
                it.position = newPos
                updateDistanceDisplay(newPos)
                drawLineToNearestTarget(newPos)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 적용할 맵 스타일: 기본 POI 제거를 위한 스타일 적용
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            )
            if (!success) {
                // 스타일 적용 실패 시 로그 남김
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 지도 유형 설정 (예: MAP_TYPE_NORMAL)
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        setupMap()
        // 정적 데이터 대신 API로 미션 정보를 가져옴
        fetchMissionPickers()

        // 지도 클릭 시 – Picker Mode에서 추가 피커 등록
        googleMap.setOnMapClickListener { latLng ->
            if (isPickMode) {
                isPickMode = false  // 선택 모드 해제
                val newPickerName = "추가 피커 $additionalPickerCount"
                additionalPickerCount++
                additionalPickerList.add(Picker(newPickerName, latLng))
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(newPickerName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
                marker?.let { additionalPickerMarkers.add(it) }
                val circle = googleMap.addCircle(
                    CircleOptions()
                        .center(latLng)
                        .radius(additionalPickerRadiusInMeters)
                        .strokeWidth(2f)
                        .strokeColor(Color.YELLOW)
                        .fillColor(0x22FFFF00)
                )
                additionalPickerCircles.add(circle)
                Toast.makeText(requireContext(), "피커가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                updateDistanceDisplay()
            }
        }
        googleMap.setOnMarkerClickListener { false }

        // Picker Mode인 경우, 초기 내 위치 설정
        if (userMarker == null) {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(INITIAL_PICKER_LATLNG, 18f)
            )
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.target_marker)
            val markerOptions = MarkerOptions().position(INITIAL_PICKER_LATLNG).title("My Location")
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.draw(canvas)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            }
            userMarker = googleMap.addMarker(markerOptions)
        }
    }

    private fun setupMap() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000         // 10초 간격
            fastestInterval = 5000     // 최소 5초 간격
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    updateUserLocation(userLatLng)
                    checkIfWithinRadius(userLatLng)
                    checkIfInsidePolygon(userLatLng)
                    updateDistanceDisplay(userLatLng)
                    drawLineToNearestTarget(userLatLng)
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, requireActivity().mainLooper
        )
    }

    // Auto Mode일 때 내 위치 업데이트 (Picker Mode에서는 고정된 위치 유지)
    private fun updateUserLocation(userLatLng: LatLng) {
        if (userMarker == null) {
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.target_marker)
            val markerOptions = MarkerOptions().position(userLatLng).title("My Location")
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.draw(canvas)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            }
            userMarker = googleMap.addMarker(markerOptions)
            if (!isUserLocationUpdated && !isPickerModeEnabled) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                isUserLocationUpdated = true
            }
        } else {
            if (!isPickerModeEnabled) {
                userMarker?.position = userLatLng
            }
        }
    }

    private fun checkIfWithinRadius(userLatLng: LatLng) {
        val combinedPickerList = getMissionPickers() + additionalPickerList
        for (picker in combinedPickerList) {
            val results = FloatArray(1)
            Location.distanceBetween(
                userLatLng.latitude, userLatLng.longitude,
                picker.location.latitude, picker.location.longitude,
                results
            )
            if (results[0] <= quizRadiusInMeters && !isQuizFragmentShown) {
                showQuizFragment()
                break
            }
        }
    }

//    private var isInsidePolygonToastShown = false
//
//    private fun checkIfInsidePolygon(userLatLng: LatLng) {
//        var isUserInside = false
//
//        missionList.forEach { mission ->
//            if (PolyUtil.containsLocation(userLatLng, mission.getEdgePointsLatLng(), true)) {
//                isUserInside = true
//                if (!isInsidePolygonToastShown) {
//                    Toast.makeText(requireContext(), "You are in ${mission.positionName ?: "미지정"}", Toast.LENGTH_SHORT).show()
//                    isInsidePolygonToastShown = true
//                }
//            }
//        }
//
//        if (!isUserInside) {
//            isInsidePolygonToastShown = false
//        }
//    }

//    // 각 미션(폴리곤) 이름과 토스트 표시 여부를 기록하는 맵
//    private val polygonToastShownMap = mutableMapOf<String, Boolean>()
//
//    private fun checkIfInsidePolygon(userLatLng: LatLng) {
//        missionList.forEach { mission ->
//            // 미션 이름이 없으면 "미지정"으로 대체
//            val polygonName = mission.positionName ?: "미지정"
//            val isInside = PolyUtil.containsLocation(userLatLng, mission.getEdgePointsLatLng(), true)
//
//            if (isInside) {
//                // 해당 폴리곤에 대해 아직 토스트가 띄워지지 않았다면
//                if (polygonToastShownMap[polygonName] != true) {
//                    Toast.makeText(requireContext(), "You are in $polygonName", Toast.LENGTH_SHORT).show()
//                    polygonToastShownMap[polygonName] = true
//                }
//            } else {
//                // 사용자가 폴리곤 밖으로 나가면, 다시 토스트를 띄울 수 있도록 플래그를 리셋 (원한다면)
//                polygonToastShownMap[polygonName] = false
//            }
//        }
//    }

    // 각 미션(폴리곤)의 missionId를 키로 하여, 해당 미션에 대해 팝업을 이미 띄웠는지 여부를 기록하는 맵
    private val missionPopupShownMap = mutableMapOf<Int, Boolean>()

    private fun checkIfInsidePolygon(userLatLng: LatLng) {
        missionList.forEach { mission ->
            val key = mission.missionId
            val isInside = PolyUtil.containsLocation(userLatLng, mission.getEdgePointsLatLng(), true)
            if (isInside) {
                // 해당 미션에 대해 아직 팝업이 띄워지지 않았다면
                if (missionPopupShownMap[key] != true) {
                    showMissionPopup(mission)
                    missionPopupShownMap[key] = true
                }
            } else {
                // 미션 영역 밖에 나가면, 다음번 재진입 시 다시 팝업이 뜰 수 있도록 리셋
                missionPopupShownMap[key] = false
            }
        }
    }

    private fun showMissionPopup(mission: Mission) {
        when (mission.codeId) {
            "M001" -> {
                // M001: 문화재 미션 발생! / "[positionName] 관련 문제를 풀고 역사 카드를 얻어봐"
                val popup = MissionExplainFragment.newInstance("M001", mission.positionName ?: "미지정", "", mission.missionId)
                popup.show(parentFragmentManager, "M001Popup")
            }
            "M002" -> {
                // M002: 랜덤 미션 발생! / "[placeName] 관련 문화재 관련 랜덤 퀴즈를 풀고 역사 카드를 얻어봐"
                val popup = MissionExplainFragment.newInstance("M002", mission.positionName ?: "미지정", userPreferences.location)
                popup.show(parentFragmentManager, "M002Popup")
            }
            "M003" -> {
                // M003: 인증샷 미션 발생! / "인증샷을 찍어서 업로드 해주세요"
                val popup = MissionExplainFragment.newInstance("M003", mission.positionName ?: "미지정", "")
                popup.show(parentFragmentManager, "M003Popup")
            }
            else -> {
                val popup = MissionExplainFragment.newInstance("default", mission.positionName ?: "미지정", "")
                popup.show(parentFragmentManager, "DefaultPopup")
            }
        }
    }

    private fun showQuizFragment() {
        isQuizFragmentShown = true
        val quizFragment = MissionExplainFragment()
        quizFragment.show(parentFragmentManager, "QuizFragment")
    }

    private fun updateDistanceDisplay(userLatLng: LatLng? = null) {
        val currentLocation = userLatLng ?: userMarker?.position
        if (currentLocation == null) {
            bottomSheetTextView.text = "위치 조회중..."
            return
        }
        val combinedPickerList = getMissionPickers() + additionalPickerList
        val pickerDistances = combinedPickerList.map { picker ->
            val results = FloatArray(1)
            Location.distanceBetween(
                currentLocation.latitude, currentLocation.longitude,
                picker.location.latitude, picker.location.longitude,
                results
            )
            Triple(picker.name, results[0].toDouble(), picker.location)
        }
        val sb = StringBuilder()
        for ((name, distance, _) in pickerDistances.sortedWith(compareBy({ it.second }, { it.first }))) {
            val distanceText = if (distance < 1000) {
                String.format("%.0f m", distance)
            } else {
                String.format("%.2f km", distance / 1000)
            }
            sb.append("$name: $distanceText\n")
        }
        bottomSheetTextView.text = sb.toString().trim()
    }

    /**
     * 피커 중 가장 가까운 대상으로 사용자 위치에서 해당 피커 방향으로 일정 길이(예, 30m)만큼의 선과 화살표 표시
     */
    private fun drawLineToNearestTarget(userLatLng: LatLng) {
        var nearestDistance = Double.MAX_VALUE
        var nearestTargetPoint: LatLng? = null
        var nearestTargetName = ""
        val combinedPickerList = getMissionPickers() + additionalPickerList
        for (picker in combinedPickerList) {
            val results = FloatArray(1)
            Location.distanceBetween(
                userLatLng.latitude, userLatLng.longitude,
                picker.location.latitude, picker.location.longitude,
                results
            )
            val d = results[0].toDouble()
            if (d < nearestDistance) {
                nearestDistance = d
                nearestTargetPoint = picker.location
                nearestTargetName = picker.name
            }
        }
        nearestTargetPoint?.let { targetPoint ->
            nearestLine?.remove()
            val arrowLineLength = 30.0  // 표시할 선 길이 (미터)
            val actualDistance = SphericalUtil.computeDistanceBetween(userLatLng, targetPoint)
            val heading = SphericalUtil.computeHeading(userLatLng, targetPoint)
            val lineEndPoint = if (actualDistance > arrowLineLength) {
                SphericalUtil.computeOffset(userLatLng, arrowLineLength, heading)
            } else {
                targetPoint
            }
            val arrowBitmapDescriptor = getArrowBitmap()
            val arrowCap = CustomCap(arrowBitmapDescriptor, 10f)
            nearestLine = googleMap.addPolyline(
                PolylineOptions()
                    .add(userLatLng, lineEndPoint)
                    .color(Color.YELLOW)
                    .width(15f)
                    .endCap(arrowCap)
            )
        }
    }

    private fun getArrowBitmap(): BitmapDescriptor {
        val drawable: Drawable = ContextCompat.getDrawable(requireContext(), android.R.drawable.arrow_up_float)
            ?: throw IllegalArgumentException("Arrow drawable not found")
        drawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP)
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    /**
     * API를 통해 POST /api/mission/pickers 요청을 보내 미션 정보를 받아옴.
     * 요청 본문은 userPreferences의 userId, roomId, location(문화유산 장소명) 값을 사용.
     * 응답 성공 시 missionList를 업데이트한 후 폴리곤과 피커를 지도에 그린다.
     */
    private fun fetchMissionPickers() {
        val userId = userPreferences.userId
        val roomId = userPreferences.roomId
        val placeName = userPreferences.location  // 문화유산 장소명

        Log.d("Map", "Response Code: ${userId} ${roomId} ${placeName}")


        if (userId == null || roomId == null || placeName.isEmpty()) {
            Toast.makeText(requireContext(), "필요한 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://i12d106.p.ssafy.io/api/mission/pickers"
        val jsonBody = """
            {
              "userId": $userId,
              "roomId": $roomId,
              "placeName": "$placeName"
            }
        """.trimIndent()
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("accept", "*/*")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "미션 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "오류: ${response.code}", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val gson = Gson()
                            val missions = gson.fromJson(responseBody, Array<Mission>::class.java).toList()
                            missionList = missions
                            requireActivity().runOnUiThread {
                                drawMissionPolygons()
                                addInitialPickerMarkers()
                            }
                        } catch (e: Exception) {
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "미션 데이터 파싱 오류", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
    }

    // API로 받아온 미션 정보를 기준으로 폴리곤을 그린다.
    private fun drawMissionPolygons() {
        drawnPolygons.forEach { it.remove() }
        drawnPolygons.clear()
        missionList.forEach { mission ->
            val (strokeColor, fillColor) = when (mission.codeId) {
                "M001" -> Pair(Color.BLUE, Color.argb(34, 0, 0, 255))
                "M002" -> Pair(Color.YELLOW, Color.argb(34, 255, 255, 0))
                "M003" -> Pair(Color.MAGENTA, Color.argb(34, 255, 0, 255))
                else -> Pair(Color.GRAY, Color.argb(34, 128, 128, 128))
            }
            val poly = googleMap.addPolygon(
                PolygonOptions()
                    .addAll(mission.getEdgePointsLatLng())
                    .strokeColor(strokeColor)
                    .fillColor(fillColor)
                    .strokeWidth(5f)
            )
            drawnPolygons.add(poly)
        }
    }

    // API로 받아온 미션 정보를 기준으로 각 미션의 중심에 마커를 표시한다.
    private fun addInitialPickerMarkers() {
        missionList.forEach { mission ->
            val markerColor = when (mission.codeId) {
                "M001" -> BitmapDescriptorFactory.HUE_BLUE
                "M002" -> BitmapDescriptorFactory.HUE_YELLOW
                "M003" -> BitmapDescriptorFactory.HUE_VIOLET
                else -> BitmapDescriptorFactory.HUE_RED
            }
            googleMap.addMarker(
                MarkerOptions()
                    .position(mission.getCenterPointLatLng())
                    .title(mission.positionName ?: "미지정")
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                setupMap()
            } else {
                Toast.makeText(requireContext(), "Location permission is required.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        for (marker in additionalPickerMarkers) {
            marker.remove()
        }
        additionalPickerMarkers.clear()
        for (circle in additionalPickerCircles) {
            circle.remove()
        }
        additionalPickerCircles.clear()
        additionalPickerList.clear()
        additionalPickerCount = 1
        nearestLine?.remove()
    }
}
