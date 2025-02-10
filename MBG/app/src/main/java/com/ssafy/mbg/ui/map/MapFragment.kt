package com.ssafy.mbg.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.ssafy.mbg.R
import com.ssafy.mbg.util.PolygonData
import com.ssafy.mbg.util.PolygonUtils

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

    // 외부 PolygonData에서 polygonCoordinates 참조 (좌표 개수는 최소 3개 ~ 20개 이상 가능)
    private val polygonCoordinates = PolygonData.polygonCoordinates
    private lateinit var polygon: Polygon

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
    // 추가된 피커 마커와 원을 추후 제거하기 위해 저장할 리스트
    private val additionalPickerMarkers = mutableListOf<Marker>()
    private val additionalPickerCircles = mutableListOf<Circle>()

    // 면적(폴리곤) 리스트 – 필요에 따라 여러 개 추가할 수 있습니다.
    private val initialAreaList = listOf(
        Area("폴리곤 영역 1", polygonCoordinates)
        // 추가 면적은 여기 추가하면 됩니다.
    )

    // 위치 선택 모드 플래그 (기존: 지도 클릭 시 추가 피커 등록)
    private var isPickMode = false
    // 추가 피커 번호 카운터 (자동 이름 부여용)
    private var additionalPickerCount = 1

    // QuizFragment에서 사용할 피커와의 반경 (100m)
    private val quizRadiusInMeters = 100.0
    // 추가된 피커에 표시할 반경 (70m, 노란색)
    private val additionalPickerRadiusInMeters = 70.0

    // 가장 가까운 대상으로 사용자 위치와 연결할 선(Polyline)
    private var nearestLine: Polyline? = null

    // 새로운 기능: Picker Mode 토글 (Auto Mode vs. Picker Mode)
    // Picker Mode일 때는 자동 위치 갱신 대신 사용자가 화살표 버튼을 통해 위치를 이동시킵니다.
    private var isPickerModeEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_map.xml에는 지도, Pick Location 버튼, 토글 버튼, 화살표 버튼, Bottom Sheet가 포함됨
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        bottomSheetTextView = view.findViewById(R.id.bottomSheetTitleTextView)

        // 지도 Fragment 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // 기존 "Pick Location" 버튼 (지도 클릭 시 추가 피커 모드 전환)
        val pickLocationButton: Button = view.findViewById(R.id.btn_pick_location)
        pickLocationButton.setOnClickListener {
            isPickMode = true
            Toast.makeText(requireContext(), "위치를 지정해주세요", Toast.LENGTH_SHORT).show()
        }

        // 토글 버튼 (Auto Mode / Picker Mode 전환)
        val toggleMode: ToggleButton = view.findViewById(R.id.toggle_mode)
        // Picker Mode일 때 사용할 화살표 버튼 컨테이너
        val arrowContainer: LinearLayout = view.findViewById(R.id.arrow_container)
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
        setupMap()
        drawPolygon()
        addInitialPickerMarkers()  // 초기 피커 3개와 초록색 원 표시

        // 최초에는 내 위치가 아직 파악되지 않았다면 초기 피커("시작점") 위치로 카메라 이동
        if (userMarker == null) {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(initialPickerList[0].location, 15f)
            )
        }

        // 지도 클릭 시 – 만약 위치 선택 모드이면 추가 피커를 등록
        googleMap.setOnMapClickListener { latLng ->
            if (isPickMode) {
                isPickMode = false // 선택 모드 해제
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

    private fun drawPolygon() {
        polygon = googleMap.addPolygon(
            PolygonOptions()
                .addAll(polygonCoordinates)
                .strokeColor(0x5500FF00)
                .fillColor(0x2200FF00)
                .strokeWidth(5f)
        )
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
                    // Auto Mode일 때만 현재 위치 자동 업데이트 (Picker Mode에서는 수동 이동)
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

    // Auto Mode일 때만 현재 위치를 업데이트 (Picker Mode에서는 수동 이동)
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
        if (PolyUtil.containsLocation(userLatLng, polygonCoordinates, true) && !isInsidePolygonToastShown) {
            Toast.makeText(requireContext(), "You are in Gumi", Toast.LENGTH_SHORT).show()
            isInsidePolygonToastShown = true
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
        val areaDistances = initialAreaList.map { area ->
            val distance = PolygonUtils.distanceToPolygon(currentLocation, area.coordinates)
            Triple(area.name, distance, PolygonUtils.closestPointOnPolygon(currentLocation, area.coordinates))
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
        for (area in initialAreaList) {
            val distance = PolygonUtils.distanceToPolygon(userLatLng, area.coordinates)
            if (distance < nearestDistance) {
                nearestDistance = distance
                nearestTargetPoint = PolygonUtils.closestPointOnPolygon(userLatLng, area.coordinates)
                nearestTargetName = area.name
            }
        }
        nearestTargetPoint?.let { targetPoint ->
            nearestLine?.remove()
            nearestLine = googleMap.addPolyline(
                PolylineOptions()
                    .add(userLatLng, targetPoint)
                    .color(Color.RED)
                    .width(5f)
            )
            // 토글 시 가장 가까운 대상을 Toast로 띄우지 않도록 주석 처리
            /*
            Toast.makeText(
                requireContext(),
                "가장 가까운 대상: $nearestTargetName (${if (nearestDistance < 1000) String.format("%.0f m", nearestDistance) else String.format("%.2f km", nearestDistance / 1000)})",
                Toast.LENGTH_SHORT
            ).show()
            */
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
