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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
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

    // 미션 관련 마커들을 저장할 리스트 (추가)
    private val missionMarkers = mutableListOf<Marker>()

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

    // 바텀 시트 내 컨테이너 (피커 리스트와 거리 표시)
    private lateinit var bottomSheetContainer: LinearLayout

//    // 데이터 클래스: Picker (이름과 좌표)
//    data class Picker(val name: String, val location: LatLng)

    // 기존 Picker 클래스 선언 부분을 아래와 같이 변경
    data class Picker(val name: String, val location: LatLng, val codeId: String? = null)

    // 미션 정보를 Picker로 변환하는 함수 (완료된 미션은 제외)
//    private fun getMissionPickers(): List<Picker> {
//        return missionList.filter { !it.correct }
//            .map { mission ->
//                Picker(mission.positionName ?: "미지정", mission.getCenterPointLatLng())
//            }
//    }
    private fun getMissionPickers(): List<Picker> {
        return missionList.filter { !it.correct }
            .map { mission ->
                Picker(
                    mission.positionName ?: "미지정",
                    mission.getCenterPointLatLng(),
                    mission.codeId    // codeId를 추가합니다.
                )
            }
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
    // AutoMode 바꿔야 하는 부분 true -> false
    private var isPickerModeEnabled = false

    // Picker Mode의 고정 초기 위치 (경복궁)
//    private val INITIAL_PICKER_LATLNG = LatLng(37.579050513803224, 126.97762422651554)
    private val INITIAL_PICKER_LATLNG = LatLng(36.107153667264036, 128.4163848449371)

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
        bottomSheetContainer = view.findViewById(R.id.bottomSheetContainer)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val toggleMode: ToggleButton = view.findViewById(R.id.toggle_mode)
        // AutoMode 바꿔야 하는 부분 true -> false
        toggleMode.isChecked = false
        val arrowContainer: GridLayout = view.findViewById(R.id.arrow_container)
        // AutoMode 바꿔야 하는 부분 VISIBLE -> GONE
        arrowContainer.visibility = View.GONE

//        toggleMode.setOnCheckedChangeListener { _, isChecked ->
//            isPickerModeEnabled = isChecked
//            if (isPickerModeEnabled) {
//                arrowContainer.visibility = View.VISIBLE
//                Toast.makeText(requireContext(), "Picker Mode 활성화", Toast.LENGTH_SHORT).show()
//            } else {
//                arrowContainer.visibility = View.GONE
//                Toast.makeText(requireContext(), "Auto Mode 활성화", Toast.LENGTH_SHORT).show()
//            }
//        }

        // 토글 버튼을 클릭했을 때, Picker Mode이면 버튼은 보이고, Auto Mode이면 버튼을 감춥니다.
        toggleMode.setOnCheckedChangeListener { buttonView, isChecked ->
            isPickerModeEnabled = isChecked
            if (isChecked) {
                arrowContainer.visibility = View.VISIBLE
                // Picker Mode일 때 토글 버튼은 보임
                buttonView.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Picker Mode 활성화", Toast.LENGTH_SHORT).show()
            } else {
                arrowContainer.visibility = View.GONE
                // Auto Mode일 때 토글 버튼을 감춤 (투명하게)
                buttonView.visibility = View.GONE
                Toast.makeText(requireContext(), "Auto Mode 활성화", Toast.LENGTH_SHORT).show()
            }
        }

        val btnArrowUp: ImageButton = view.findViewById(R.id.btn_arrow_up)
        val btnArrowDown: ImageButton = view.findViewById(R.id.btn_arrow_down)
        val btnArrowLeft: ImageButton = view.findViewById(R.id.btn_arrow_left)
        val btnArrowRight: ImageButton = view.findViewById(R.id.btn_arrow_right)
        val moveDistance = 10.0

        btnArrowUp.setOnClickListener {
            userMarker?.let {
                val newPos = SphericalUtil.computeOffset(it.position, moveDistance, 0.0)
                it.position = newPos
                updateDistanceDisplay(newPos)
                drawLineToNearestTarget(newPos)
                checkIfInsidePolygon(newPos)
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
        // ★ 방법 2: parentFragmentManager 사용하여 "refreshMission" 이벤트 수신
        parentFragmentManager.setFragmentResultListener("refreshMission", viewLifecycleOwner) { key, bundle ->
            fetchMissionPickers()
//            userMarker?.position?.let { checkIfInsidePolygon(it) }
        }
    }

//    override fun onMapReady(map: GoogleMap) {
//        googleMap = map
//        try {
//            val success = googleMap.setMapStyle(
//                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
//            )
//            if (!success) {
//                // 스타일 적용 실패 시 로그 남김
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
//
//        setupMap()
//        fetchMissionPickers()
//
//        googleMap.setOnMapClickListener { latLng ->
//            if (isPickMode) {
//                isPickMode = false
//                val newPickerName = "추가 피커 $additionalPickerCount"
//                additionalPickerCount++
//                additionalPickerList.add(Picker(newPickerName, latLng))
//                val marker = googleMap.addMarker(
//                    MarkerOptions()
//                        .position(latLng)
//                        .title(newPickerName)
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                )
//                marker?.let { additionalPickerMarkers.add(it) }
//                val circle = googleMap.addCircle(
//                    CircleOptions()
//                        .center(latLng)
//                        .radius(additionalPickerRadiusInMeters)
//                        .strokeWidth(2f)
//                        .strokeColor(Color.YELLOW)
//                        .fillColor(0x22FFFF00)
//                )
//                additionalPickerCircles.add(circle)
//                Toast.makeText(requireContext(), "피커가 추가되었습니다.", Toast.LENGTH_SHORT).show()
//                updateDistanceDisplay()
//            }
//        }
//        googleMap.setOnMarkerClickListener { false }
//
//        if (userMarker == null) {
//            googleMap.moveCamera(
//                CameraUpdateFactory.newLatLngZoom(INITIAL_PICKER_LATLNG, 18f)
//            )
//            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.target_marker)
//            val markerOptions = MarkerOptions().position(INITIAL_PICKER_LATLNG).title("My Location")
//            if (drawable != null) {
//                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
//                val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
//                val canvas = Canvas(bitmap)
//                drawable.draw(canvas)
//                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//            }
//            userMarker = googleMap.addMarker(markerOptions)
//        }
//    }

    // onMapReady() 메서드 수정
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
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
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        setupMap()
        fetchMissionPickers()

        // 현재 위치를 가져와서 지도를 그 위치로 이동시키고 확대/틸트 설정
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    updateUserLocation(userLatLng)

                    // CameraPosition 설정 (확대 수준, 틸트, 방향)
                    val cameraPosition = CameraPosition.Builder()
                        .target(userLatLng)     // 중심 위치
                        .zoom(18f)              // 확대 수준 (18f가 매우 확대된 수준)
                        .tilt(45f)              // 틸트(기울기) 설정 (45도)
                        .build()

                    // 카메라 이동
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
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
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    updateUserLocation(userLatLng)
//                    checkIfWithinRadius(userLatLng)
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

//    private fun checkIfWithinRadius(userLatLng: LatLng) {
//        val combinedPickerList = getMissionPickers() + additionalPickerList
//        for (picker in combinedPickerList) {
//            val results = FloatArray(1)
//            Location.distanceBetween(
//                userLatLng.latitude, userLatLng.longitude,
//                picker.location.latitude, picker.location.longitude,
//                results
//            )
//            if (results[0] <= quizRadiusInMeters && !isQuizFragmentShown) {
//                showQuizFragment()
//                break
//            }
//        }
//    }

    private val missionPopupShownMap = mutableMapOf<Int, Boolean>()

//    private fun checkIfInsidePolygon(userLatLng: LatLng) {
//        missionList.forEach { mission ->
//            val key = mission.missionId
//            val isInside = PolyUtil.containsLocation(userLatLng, mission.getEdgePointsLatLng(), true)
//            if (isInside) {
//                if (missionPopupShownMap[key] != true) {
//                    showMissionPopup(mission)
//                    missionPopupShownMap[key] = true
//                }
//            } else {
//                missionPopupShownMap[key] = false
//            }
//        }
//    }

    private fun checkIfInsidePolygon(userLatLng: LatLng) {
        // 여기서는 완료된 미션(correct==true)은 무시합니다.
        missionList.forEach { mission ->
            if (mission.correct) return@forEach
            val key = mission.missionId
            val isInside = PolyUtil.containsLocation(userLatLng, mission.getEdgePointsLatLng(), true)
            if (isInside) {
                if (missionPopupShownMap[key] != true) {
                    showMissionPopup(mission)
                    missionPopupShownMap[key] = true
                }
            } else {
                missionPopupShownMap[key] = false
            }
        }
    }
    private fun showMissionPopup(mission: Mission) {
        when (mission.codeId) {
            "M001" -> {
                val popup = MissionExplainFragment.newInstance("M001", mission.positionName ?: "미지정", "", mission.missionId)
                popup.show(parentFragmentManager, "M001Popup")
            }
            "M002" -> {
                val popup = MissionExplainFragment.newInstance("M002", mission.positionName ?: "미지정", userPreferences.location, mission.missionId)
                popup.show(parentFragmentManager, "M002Popup")
            }
            "M003" -> {

                // M003 미션은 조장(j001)에게만 팝업을 띄웁니다.
                if (userPreferences.codeId == "J001") {
                    val photoFragment = PhotoMissionFragment.newInstance(
                        "M003",
                        mission.positionName ?: "미지정",
                        userPreferences.location,
                        mission.missionId
                    )
                    photoFragment.show(parentFragmentManager, "PhotoMissionFragment")
                } else {
                    // 조원(j002)은 팝업이 뜨지 않도록 처리합니다.
//                    Toast.makeText(requireContext(), "이 미션은 조장 전용입니다.", Toast.LENGTH_SHORT).show()
                }
            }
//            else -> {
//                val popup = MissionExplainFragment.newInstance("default", mission.positionName ?: "미지정", "", mission.missionId)
//                popup.show(parentFragmentManager, "DefaultPopup")
//            }
        }
    }

    private fun showQuizFragment() {
        isQuizFragmentShown = true
        val quizFragment = MissionExplainFragment()
        quizFragment.show(parentFragmentManager, "QuizFragment")
    }

    /**
     * 미션 목록 중, 완료되지 않은 미션( correct == false )과 추가 피커 목록을 이용하여
     * 거리를 계산하고, 바텀 시트 컨테이너에 항목들을 추가합니다.
     */
//    private fun updateDistanceDisplay(userLatLng: LatLng? = null) {
//        val currentLocation = userLatLng ?: userMarker?.position
//        bottomSheetContainer.removeAllViews()
//        if (currentLocation == null) {
//            val tv = AppCompatTextView(requireContext())
//            tv.text = "위치 조회중..."
//            tv.gravity = Gravity.CENTER
//            bottomSheetContainer.addView(tv)
//            return
//        }
//        val combinedPickerList = getMissionPickers() + additionalPickerList
//        val pickerDistances = combinedPickerList.map { picker ->
//            val results = FloatArray(1)
//            Location.distanceBetween(
//                currentLocation.latitude, currentLocation.longitude,
//                picker.location.latitude, picker.location.longitude,
//                results
//            )
//            Triple(picker.name, results[0].toDouble(), picker.location)
//        }
//        val sortedList = pickerDistances.sortedWith(compareBy({ it.second }, { it.first }))
//        for ((name, distance, _) in sortedList) {
//            val distanceText = if (distance < 1000) {
//                String.format("%.0f m", distance)
//            } else {
//                String.format("%.2f km", distance / 1000)
//            }
//            val tv = AppCompatTextView(requireContext()).apply {
//                text = "$name: $distanceText"
//                gravity = Gravity.CENTER
//                setPadding(16, 16, 16, 16)
//                setBackgroundResource(R.drawable.bg_distance_item)
//                val params = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                params.setMargins(8, 8, 8, 8)
//                layoutParams = params
//            }
//            bottomSheetContainer.addView(tv)
//        }
//    }

    // 위 버전이 원본 아래가 수정 버전
//    private fun updateDistanceDisplay(userLatLng: LatLng? = null) {
//        val currentLocation = userLatLng ?: userMarker?.position
//        bottomSheetContainer.removeAllViews()
//        if (currentLocation == null) {
//            val tv = AppCompatTextView(requireContext())
//            tv.text = "위치 조회중..."
//            tv.gravity = Gravity.CENTER
//            bottomSheetContainer.addView(tv)
//            return
//        }
//        val combinedPickerList = getMissionPickers() + additionalPickerList
//        val pickerDistances = combinedPickerList.map { picker ->
//            val results = FloatArray(1)
//            Location.distanceBetween(
//                currentLocation.latitude, currentLocation.longitude,
//                picker.location.latitude, picker.location.longitude,
//                results
//            )
//            Triple(picker.name, results[0].toDouble(), picker.location)
//        }
//        val sortedList = pickerDistances.sortedWith(compareBy({ it.second }, { it.first }))
//        for ((name, distance, _) in sortedList) {
//            val distanceText = if (distance < 1000) {
//                String.format("%.0f m", distance)
//            } else {
//                String.format("%.2f km", distance / 1000)
//            }
//
//            // 수평 정렬 LinearLayout 생성
//            val itemLayout = LinearLayout(requireContext()).apply {
//                orientation = LinearLayout.HORIZONTAL
//                layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                ).apply {
//                    setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
//                }
//                setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
//                setBackgroundResource(R.drawable.bg_distance_item)
//            }
//
//            // 좌측에 이름 표시 TextView (Bold, 왼쪽 정렬, 가중치 1)
//            val nameTextView = AppCompatTextView(requireContext()).apply {
//                text = name
//                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
//                gravity = Gravity.START or Gravity.CENTER_VERTICAL
//                setTypeface(null, android.graphics.Typeface.BOLD)
//                textSize = 16f
//                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            }
//
//            // 우측에 거리 표시 TextView (Bold, 오른쪽 정렬)
//            val distanceTextView = AppCompatTextView(requireContext()).apply {
//                text = distanceText
//                layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                gravity = Gravity.END or Gravity.CENTER_VERTICAL
//                setTypeface(null, android.graphics.Typeface.BOLD)
//                textSize = 16f
//                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            }
//
//            itemLayout.addView(nameTextView)
//            itemLayout.addView(distanceTextView)
//            bottomSheetContainer.addView(itemLayout)
//        }
//    }

    private fun updateDistanceDisplay(userLatLng: LatLng? = null) {
        val currentLocation = userLatLng ?: userMarker?.position
        bottomSheetContainer.removeAllViews()
        if (currentLocation == null) {
            val tv = AppCompatTextView(requireContext())
            tv.text = "위치 조회중..."
            tv.gravity = Gravity.CENTER
            bottomSheetContainer.addView(tv)
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
            Triple(picker, results[0].toDouble(), picker.location)
        }
        val sortedList = pickerDistances.sortedWith(compareBy({ it.second }, { it.first.name }))
        for ((picker, distance, _) in sortedList) {
            val distanceText = if (distance < 1000) {
                String.format("%.0f m", distance)
            } else {
                String.format("%.2f km", distance / 1000)
            }

            // 수평 LinearLayout 생성
            val itemLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
                }
                setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
                setBackgroundResource(R.drawable.bg_distance_item)
            }

            // 좌측에 이름 표시 (Bold, 왼쪽 정렬) – codeId에 따라 색상 지정
            val nameTextView = AppCompatTextView(requireContext()).apply {
                text = picker.name
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                gravity = Gravity.START or Gravity.CENTER_VERTICAL
                setTypeface(null, android.graphics.Typeface.BOLD)
                textSize = 16f
                when (picker.codeId) {
                    "M001" -> setTextColor(Color.parseColor("#4E3CCC"))
//                    "M002" -> setTextColor(Color.YELLOW)
                    "M002" -> setTextColor(Color.parseColor("#E9E94D"))
                    "M003" -> setTextColor(Color.parseColor("#C0A020F0"))
                    else   -> setTextColor(Color.BLACK)
                }
            }

            // 우측에 거리 표시 (Bold, 오른쪽 정렬)
            val distanceTextView = AppCompatTextView(requireContext()).apply {
                text = distanceText
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
                setTypeface(null, android.graphics.Typeface.BOLD)
                textSize = 16f
                setTextColor(Color.BLACK)
            }

            itemLayout.addView(nameTextView)
            itemLayout.addView(distanceTextView)
            bottomSheetContainer.addView(itemLayout)
        }
    }

    /**
     * 사용자 위치에서 가장 가까운 피커 방향으로 선을 그리고, 화살표를 표시합니다.
     */
    private fun drawLineToNearestTarget(userLatLng: LatLng) {
        var nearestDistance = Double.MAX_VALUE
        var nearestTargetPoint: LatLng? = null
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
            }
        }
        nearestTargetPoint?.let { targetPoint ->
            nearestLine?.remove()
            val arrowLineLength = 30.0
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
                    .color(Color.GREEN)
                    .width(15f)
                    .endCap(arrowCap)
            )
        }
    }

    private fun getArrowBitmap(): BitmapDescriptor {
        val drawable: Drawable = ContextCompat.getDrawable(requireContext(), android.R.drawable.arrow_up_float)
            ?: throw IllegalArgumentException("Arrow drawable not found")
        drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP)
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

    // 완료된 미션에 대해서 회색 마커 아이콘을 생성하는 함수
    private fun getGrayMarker(): BitmapDescriptor {
        val drawable: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.target_marker)
        return if (drawable != null) {
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.draw(canvas)
            BitmapDescriptorFactory.fromBitmap(bitmap)
        } else {
            BitmapDescriptorFactory.defaultMarker(0f)
        }
    }

    /**
     * API를 통해 POST /api/mission/pickers 요청을 보내 미션 정보를 받아오고,
     * 응답 성공 시 missionList를 업데이트한 후 폴리곤과 피커를 지도에 그립니다.
     */
    private fun fetchMissionPickers() {
        val userId = userPreferences.userId
        val roomId = userPreferences.roomId
        val placeName = userPreferences.location

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

    private fun drawMissionPolygons() {
        drawnPolygons.forEach { it.remove() }
        drawnPolygons.clear()
        missionList.forEach { mission ->
            val (strokeColor, fillColor) = if (mission.correct) {
                Pair(Color.GRAY, Color.argb(34, 128, 128, 128))
            } else {
                when (mission.codeId) {
                    "M001" -> Pair(Color.parseColor("#5CF2ED"), Color.argb(160, 30, 0, 255))
//                    "M002" -> Pair(Color.CYAN, Color.argb(128, 255, 255, 0))
                    "M002" -> Pair(Color.parseColor("#5CF2ED"), Color.argb(128, 255, 255, 0))
                    "M003" -> Pair(Color.parseColor("#5CF2ED"), Color.argb(192, 160, 32, 240))
                    else -> Pair(Color.RED, Color.argb(34, 255, 0, 0))
                }
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

//    private fun addInitialPickerMarkers() {
//        // 기존 미션 마커 모두 제거
//        missionMarkers.forEach { it.remove() }
//        missionMarkers.clear()
//
//        missionList.forEach { mission ->
//            val markerOptions = MarkerOptions().position(mission.getCenterPointLatLng())
//            if (mission.correct) {
//                markerOptions.title("수행한 미션")
//                    .icon(getGrayMarker())
//            } else {
//                val markerColor = when (mission.codeId) {
//                    "M001" -> BitmapDescriptorFactory.HUE_BLUE
//                    "M002" -> BitmapDescriptorFactory.HUE_YELLOW
//                    "M003" -> BitmapDescriptorFactory.HUE_VIOLET
//                    else -> BitmapDescriptorFactory.HUE_RED
//                }
//                markerOptions.title(mission.positionName ?: "미지정")
//                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
//            }
//            googleMap.addMarker(markerOptions)?.let { missionMarkers.add(it) }
//        }
//    }

    private fun addInitialPickerMarkers() {
        // 기존 미션 마커 모두 제거
        missionMarkers.forEach { it.remove() }
        missionMarkers.clear()

        missionList.forEach { mission ->
            val markerOptions = MarkerOptions().position(mission.getCenterPointLatLng())
            if (mission.correct) {
                markerOptions.title("수행한 미션")
                    .icon(getGrayMarker())
            } else {
                markerOptions.title(mission.positionName ?: "미지정")
                // 미션 코드에 따라 다른 아이콘 지정
                val customIcon: BitmapDescriptor? = when (mission.codeId) {
                    "M001" -> getBitmapDescriptorFromResource(R.drawable.heritage_picker)
                    "M002" -> getBitmapDescriptorFromResource(R.drawable.random_picker)
                    "M003" -> getBitmapDescriptorFromResource(R.drawable.camera_picker)
                    else -> null
                }
                if (customIcon != null) {
                    markerOptions.icon(customIcon)
                } else {
                    // 기본 마커 아이콘 처리
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                }
            }
            googleMap.addMarker(markerOptions)?.let { missionMarkers.add(it) }
        }
    }

    // dp를 픽셀로 변환하는 함수
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    // Drawable 리소스를 원하는 크기(dp 기준)로 BitmapDescriptor로 변환하는 함수
    private fun getBitmapDescriptorFromResource(resId: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(requireContext(), resId)
            ?: throw IllegalArgumentException("Resource not found")
        // 원하는 마커 크기를 dp 단위로 설정 (예: 40dp x 40dp)
        val markerWidth = dpToPx(60)
        val markerHeight = dpToPx(60)
        drawable.setBounds(0, 0, markerWidth, markerHeight)
        val bitmap = Bitmap.createBitmap(markerWidth, markerHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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
