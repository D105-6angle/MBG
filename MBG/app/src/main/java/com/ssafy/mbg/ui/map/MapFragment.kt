package com.ssafy.mbg.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.ToggleButton
import android.widget.GridLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.ssafy.mbg.R
import com.ssafy.mbg.util.PolygonData
import com.ssafy.mbg.util.PolygonUtils
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

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

    // 여러 개의 폴리곤 좌표 집합을 참조
    private val polygonList: List<List<LatLng>> = PolygonData.polygonList
    // 그려진 폴리곤들을 저장할 리스트
    private val drawnPolygons = mutableListOf<Polygon>()

    // Bottom Sheet 내 텍스트뷰 (피커 리스트와 거리 표시)
    private lateinit var bottomSheetTextView: androidx.appcompat.widget.AppCompatTextView

    // 데이터 클래스: Picker (이름과 좌표)
    data class Picker(val name: String, val location: LatLng)
    // 데이터 클래스: Area (이름과 좌표 리스트)
    data class Area(val name: String, val coordinates: List<LatLng>)

    // 기존 피커 리스트 (초기 피커)
    private val initialPickerList = listOf(
        Picker("시작점", LatLng(36.103866, 128.418393)),
        Picker("A구역", LatLng(36.1072940, 128.4180058)),
        Picker("B구역", LatLng(36.1119887, 128.4147123))
    )

    // 추가된 피커 리스트 (동적으로 추가됨)
    private val additionalPickerList = mutableListOf<Picker>()
    // 추가된 피커 마커와 원을 제거하기 위한 리스트
    private val additionalPickerMarkers = mutableListOf<Marker>()
    private val additionalPickerCircles = mutableListOf<Circle>()

    // 면적(폴리곤) 리스트 – polygonList의 각 폴리곤을 Area로 매핑
    private val initialAreaList = polygonList.mapIndexed { index, coordinates ->
        Area("폴리곤 영역 ${index + 1}", coordinates)
    }

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

    // 모드 토글: Picker Mode vs. Auto Mode
    // 기본 모드는 Picker Mode (즉, 수동 이동)로 설정
    private var isPickerModeEnabled = true

    // Picker Mode의 고정 초기 위치 (경복궁)
    private val INITIAL_PICKER_LATLNG = LatLng(37.57640594972532, 126.97686654390287)

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

        // 적용할 맵 스타일: 기본 POI(경복궁 등) 제거를 위한 스타일 적용
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

        // 지도 유형을 위성/하이브리드로 설정하여 상세한 위성 사진처럼 표시
//        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        setupMap()
        drawPolygons()  // 모든 폴리곤 그리기
        addInitialPickerMarkers()  // 초기 피커들 그리기

        // 기본 모드가 Picker Mode이므로 초기 내 위치를 고정 좌표로 설정하고, 상세하게 보이도록 높은 줌(18f)으로 이동
        if (userMarker == null) {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(INITIAL_PICKER_LATLNG, 18f)
            )
            // Picker Mode에서는 자동 갱신 없이 고정 좌표를 사용하므로, 마커를 직접 생성
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

    // 모든 폴리곤을 그리는 함수
    private fun drawPolygons() {
        polygonList.forEach { coordinates ->
            val poly = googleMap.addPolygon(
                PolygonOptions()
                    .addAll(coordinates)
                    .strokeColor(0x5500FF00)
                    .fillColor(0x2200FF00)
                    .strokeWidth(5f)
            )
            drawnPolygons.add(poly)
        }
    }

    private fun addInitialPickerMarkers() {
        for (picker in initialPickerList) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(picker.location)
                    .title(picker.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
            googleMap.addCircle(
                CircleOptions()
                    .center(picker.location)
                    .radius(100.0)
                    .strokeWidth(2f)
                    .strokeColor(Color.GREEN)
                    .fillColor(0x2200FF00)
            )
        }
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
                    // Auto Mode일 때만 GPS 기반 자동 업데이트 실행
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

    // Auto Mode일 때만 내 위치 업데이트 (Picker Mode에서는 고정된 위치 유지)
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
            // Picker Mode일 때는 초기 위치 고정 및 높은 줌으로 이동
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
        val combinedPickerList = initialPickerList + additionalPickerList
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

    private var isInsidePolygonToastShown = false
    private fun checkIfInsidePolygon(userLatLng: LatLng) {
        // 모든 폴리곤에 대해 내부 포함 여부 확인
        polygonList.forEach { coordinates ->
            if (PolyUtil.containsLocation(userLatLng, coordinates, true) && !isInsidePolygonToastShown) {
                Toast.makeText(requireContext(), "You are in Gumi", Toast.LENGTH_SHORT).show()
                isInsidePolygonToastShown = true
            }
        }
    }

    private fun showQuizFragment() {
        isQuizFragmentShown = true
        val quizFragment = RandomQuizFragment()
        quizFragment.show(parentFragmentManager, "QuizFragment")
    }

    private fun updateDistanceDisplay(userLatLng: LatLng? = null) {
        val currentLocation = userLatLng ?: userMarker?.position
        if (currentLocation == null) {
            bottomSheetTextView.text = "위치 조회중..."
            return
        }
        val combinedPickerList = initialPickerList + additionalPickerList
        val pickerDistances = combinedPickerList.map { picker ->
            val results = FloatArray(1)
            Location.distanceBetween(
                currentLocation.latitude, currentLocation.longitude,
                picker.location.latitude, picker.location.longitude,
                results
            )
            Triple(picker.name, results[0].toDouble(), picker.location)
        }
        // 면적(폴리곤)의 경우, 모든 폴리곤에 대해 거리 계산
        val areaDistances = polygonList.flatMapIndexed { index, coordinates ->
            val distance = PolygonUtils.distanceToPolygon(currentLocation, coordinates)
            listOf(Triple("폴리곤 영역 ${index + 1}", distance, PolygonUtils.closestPointOnPolygon(currentLocation, coordinates)))
        }
        val allDistances = (pickerDistances + areaDistances)
            .sortedWith(compareBy({ it.second }, { it.first }))
        val sb = StringBuilder()
        for ((name, distance, _) in allDistances) {
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
     * 기존에는 userLatLng와 targetPoint를 단순히 선으로 연결하여 polyline을 그렸었다.
     * 아래는 대상까지의 전체 선 대신, 사용자 위치에서 대상 방향으로 일정 길이(예, 30m)만큼 오프셋한 선을 그리고,
     * 화살표 모양의 CustomCap을 적용하여 방향을 표시하는 코드입니다.
     */
    private fun drawLineToNearestTarget(userLatLng: LatLng) {
        var nearestDistance = Double.MAX_VALUE
        var nearestTargetPoint: LatLng? = null
        var nearestTargetName = ""
        val combinedPickerList = initialPickerList + additionalPickerList
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
        for (coordinates in polygonList) {
            val distance = PolygonUtils.distanceToPolygon(userLatLng, coordinates)
            if (distance < nearestDistance) {
                nearestDistance = distance
                nearestTargetPoint = PolygonUtils.closestPointOnPolygon(userLatLng, coordinates)
                nearestTargetName = "폴리곤 영역" // 필요시 인덱스를 추가할 수 있음
            }
        }
        nearestTargetPoint?.let { targetPoint ->
            nearestLine?.remove()

            // **변경된 부분:** 사용자 위치에서 대상 방향으로 일정 길이만큼의 선(예, 30m)을 그리도록 함.
            val arrowLineLength = 30.0  // 표시할 선의 길이 (미터)
            val actualDistance = SphericalUtil.computeDistanceBetween(userLatLng, targetPoint)
            val heading = SphericalUtil.computeHeading(userLatLng, targetPoint)
            // 실제 거리가 arrowLineLength보다 크면 arrowLineLength만큼 떨어진 점, 아니면 실제 대상 지점을 사용
            val lineEndPoint = if (actualDistance > arrowLineLength) {
                SphericalUtil.computeOffset(userLatLng, arrowLineLength, heading)
            } else {
                targetPoint
            }

            val arrowBitmapDescriptor = getArrowBitmap()
            // CustomCap 생성 시 두 번째 파라미터(여기서는 10f)는 기준 너비 (픽셀) – 필요에 따라 조정
            val arrowCap = CustomCap(arrowBitmapDescriptor, 15f)
            nearestLine = googleMap.addPolyline(
                PolylineOptions()
                    .add(userLatLng, lineEndPoint)
                    .color(Color.YELLOW)
                    .width(15f)
                    .endCap(arrowCap).color(Color.YELLOW)
            )
            // 기존 Toast 메시지 등은 주석 처리
            // Toast.makeText(requireContext(), "가장 가까운 대상: $nearestTargetName (${if (nearestDistance < 1000) String.format("%.0f m", nearestDistance) else String.format("%.2f km", nearestDistance / 1000)})", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * userMarker와 targetPoint를 잇는 polyline의 끝에 사용할 화살표 아이콘을 BitmapDescriptor로 반환
     */
    private fun getArrowBitmap(): BitmapDescriptor {
        // 시스템 기본 화살표 아이콘 사용 (필요에 따라 커스텀 drawable로 교체)
        val drawable: Drawable = ContextCompat.getDrawable(requireContext(), android.R.drawable.arrow_up_float)
            ?: throw IllegalArgumentException("Arrow drawable not found")

        // 원하는 색 (예: 노란색)으로 색상 필터 적용
        drawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP)

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
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
