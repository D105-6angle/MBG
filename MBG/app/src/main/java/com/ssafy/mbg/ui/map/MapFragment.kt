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
import android.widget.TextView
import android.widget.Toast
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
import com.ssafy.mbg.R

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // 사용자 위치 마커
    private var userMarker: Marker? = null
    // QuizFragment를 한 번만 보여주기 위한 플래그
    private var isQuizFragmentShown = false
    // 내 위치가 파악되어 카메라 이동한 여부
    private var isUserLocationUpdated = false

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    // 폴리곤 좌표 리스트 (예시 좌표)
    private val polygonCoordinates = listOf(
        LatLng(36.10785, 128.4142), // 좌상
        LatLng(36.10829, 128.4217), // 우상
        LatLng(36.10394, 128.4229), // 우하
        LatLng(36.10233, 128.4152)  // 좌하
    )
    private lateinit var polygon: Polygon

    // Bottom Sheet 내 텍스트뷰 (피커 리스트와 거리 표시)
    private lateinit var bottomSheetTextView: TextView

    // 피커 반경 (100m)
    private val radiusInMeters = 100.0

    // 데이터 클래스: Picker (이름과 좌표)
    data class Picker(val name: String, val location: LatLng)

    // 피커 리스트 – 첫 번째가 초기 피커(“시작점”), 이후 추가 피커
    private val pickerList = listOf(
        Picker("시작점", LatLng(36.103866, 128.418393)),
        Picker("A구역", LatLng(36.1072940, 128.4180058)),
        Picker("B구역", LatLng(36.1119887, 128.4147123))
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_map.xml에는 지도, pick 버튼, Bottom Sheet가 포함됨
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Bottom Sheet 내 텍스트뷰 초기화
        bottomSheetTextView = view.findViewById(R.id.bottomSheetTitleTextView)

        // 지도 Fragment 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // 위치 선택 버튼 – 이제는 피커 마커를 직접 누르도록 유도
        val pickLocationButton: Button = view.findViewById(R.id.btn_pick_location)
        pickLocationButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "피커 마커를 눌러 이름을 확인하세요.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        setupMap()
        drawPolygon()
        addPickerMarkers()

        // 내 위치가 아직 파악되지 않았다면 초기 피커(“시작점”) 위치로 카메라 이동
        if (userMarker == null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickerList[0].location, 15f))
        }

        // 마커 클릭 시 기본 인포윈도우가 표시됨 (필요 시 추가 로직 구현 가능)
        googleMap.setOnMarkerClickListener { marker ->
            false // false 반환 시 기본 동작(인포윈도우 표시)
        }
    }

    private fun setupMap() {
        // 위치 권한 체크
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
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

    // 모든 피커에 대한 마커 추가 (각 마커에는 피커 이름이 title로 표시됨)
    private fun addPickerMarkers() {
        for (picker in pickerList) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(picker.location)
                    .title(picker.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
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
                    updateUserLocation(userLatLng)
                    checkIfWithinRadius(userLatLng)
                    checkIfInsidePolygon(userLatLng)
                    updateDistanceDisplay(userLatLng)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            requireActivity().mainLooper
        )
    }

    // 사용자 위치 마커 생성 및 업데이트
    // 최초 위치 파악 시 카메라를 사용자 위치로 이동
    private fun updateUserLocation(userLatLng: LatLng) {
        if (userMarker == null) {
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.target_marker)
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.draw(canvas)
                val customMarker = BitmapDescriptorFactory.fromBitmap(bitmap)
                userMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("My Location")
                        .icon(customMarker)
                )
            } else {
                userMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("My Location")
                )
            }
            // 최초 위치 파악 시 카메라 이동
            if (!isUserLocationUpdated) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                isUserLocationUpdated = true
            }
        } else {
            userMarker?.position = userLatLng
        }
    }

    // 각 피커와의 거리가 100m 이내인지 확인하여 QuizFragment를 띄움 (한 번만 실행)
    private fun checkIfWithinRadius(userLatLng: LatLng) {
        for (picker in pickerList) {
            val distance = FloatArray(1)
            Location.distanceBetween(
                userLatLng.latitude, userLatLng.longitude,
                picker.location.latitude, picker.location.longitude,
                distance
            )
            if (distance[0] <= radiusInMeters && !isQuizFragmentShown) {
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
        val quizFragment = QuizFragment()
        quizFragment.show(parentFragmentManager, "QuizFragment")
    }

    /**
     * Bottom Sheet에 사용자 위치와 각 피커 간의 거리 리스트를 업데이트한다.
     * - 사용자 위치가 파악되지 않으면 "위치 조회중..."을 표시.
     * - 사용자 위치가 파악되면 피커들을 거리(가까운 순)로 정렬하여 표시하며,
     *   거리가 동일할 경우 이름 순으로 정렬한다.
     */
    private fun updateDistanceDisplay(userLatLng: LatLng? = null) {
        val currentLocation = userLatLng ?: userMarker?.position
        if (currentLocation == null) {
            bottomSheetTextView.text = "위치 조회중..."
            return
        }

        // 각 피커와의 거리를 계산
        val pickerDistances = pickerList.map { picker ->
            val results = FloatArray(1)
            Location.distanceBetween(
                currentLocation.latitude, currentLocation.longitude,
                picker.location.latitude, picker.location.longitude,
                results
            )
            Triple(picker.name, results[0], picker.location)
        }

        // 정렬: 거리(오름차순, 즉 가까운 순) → 거리가 같으면 이름 순 (알파벳 순)
        val sortedPickers = pickerDistances.sortedWith(compareBy({ it.second }, { it.first }))

        // 각 피커에 대해 "이름: 거리" 형태의 문자열 구성
        val sb = StringBuilder()
        for ((name, distance, _) in sortedPickers) {
            val distanceText = if (distance < 1000) {
                String.format("%.0f m", distance)
            } else {
                String.format("%.2f km", distance / 1000)
            }
            sb.append("$name: $distanceText\n")
        }
        bottomSheetTextView.text = sb.toString().trim()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                setupMap()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
