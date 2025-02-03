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

    private var userMarker: Marker? = null          // 사용자 위치 마커
    private var targetMarker: Marker? = null        // 타겟(피커) 위치 마커
    private var targetCircle: Circle? = null        // 타겟 위치의 반경 원
    private val radiusInMeters = 100.0              // 타겟 반경 (100m)
    private var isPickMode = false                  // 위치 선택 모드 상태
    private var isQuizFragmentShown = false         // QuizFragment 표시 여부

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    // 폴리곤 좌표 리스트 (예시 좌표)
    private val polygonCoordinates = listOf(
        LatLng(36.10785, 128.4142), // 좌상
        LatLng(36.10829, 128.4217), // 우상
        LatLng(36.10394, 128.4229), // 우하
        LatLng(36.10233, 128.4152)  // 좌하
    )
    private lateinit var polygon: Polygon

    // Bottom Sheet 내 거리 표시 TextView
    private lateinit var bottomSheetTextView: TextView

    // 초기 피커(타겟) 위치
    private val initialPickerLocation = LatLng(36.103866, 128.418393)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_map.xml에는 지도, pick 버튼, bottom sheet가 포함됨
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // bottom sheet 내 TextView 초기화
        bottomSheetTextView = view.findViewById(R.id.bottomSheetTitleTextView)

        // 지도 Fragment 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // 위치 선택 버튼 설정
        val pickLocationButton: Button = view.findViewById(R.id.btn_pick_location)
        pickLocationButton.setOnClickListener {
            isPickMode = true
            Toast.makeText(
                requireContext(),
                "Tap on the map to pick a location.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        setupMap()
        drawPolygon()

        // 초기 카메라 위치를 피커 위치로 바로 이동 (moveCamera 제거, setTargetLocation 내부에서 카메라 이동)
        setTargetLocation(initialPickerLocation, immediate = true)

        // 지도 클릭 시 위치 선택 (피커 모드)
        googleMap.setOnMapClickListener { latLng ->
            if (isPickMode) {
                isPickMode = false // 선택 모드 해제
                setTargetLocation(latLng, immediate = false)
                Toast.makeText(requireContext(), "Target location set!", Toast.LENGTH_SHORT).show()
            }
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

    /**
     * 타겟 위치를 설정하고 카메라를 이동한다.
     * @param latLng 설정할 위치
     * @param immediate true이면 즉시, false이면 애니메이션으로 카메라 이동
     */
    private fun setTargetLocation(latLng: LatLng, immediate: Boolean = false) {
        // 기존 타겟 마커와 원 제거
        targetMarker?.remove()
        targetCircle?.remove()

        // 타겟 마커 추가 (노란색)
        targetMarker = googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Target Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        )

        // 타겟 반경 원 추가
        targetCircle = googleMap.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(radiusInMeters)
                .strokeWidth(2f)
                .strokeColor(Color.YELLOW)
                .fillColor(0x22F5F5F5)
        )

        // 카메라 이동: immediate가 true이면 즉시, 아니면 애니메이션 효과로 이동
        if (immediate) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }

        updateDistanceDisplay()
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000         // 10초 간격
            fastestInterval = 5000   // 최소 5초 간격
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    updateUserLocation(userLatLng)
                    checkIfWithinRadius(userLatLng)
                    checkIfInsidePolygon(userLatLng)
                    // 사용자 위치 업데이트 시마다 거리 표시 업데이트
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

    private fun updateUserLocation(userLatLng: LatLng) {
        if (userMarker == null) {
            // 커스텀 벡터(drawable)를 Bitmap으로 변환하여 마커로 사용
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
                // drawable 로드 실패 시 기본 마커 사용
                userMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("My Location")
                )
            }
        } else {
            userMarker?.position = userLatLng
        }
    }

    private fun checkIfWithinRadius(userLatLng: LatLng) {
        targetCircle?.center?.let { center ->
            val distance = FloatArray(1)
            Location.distanceBetween(
                userLatLng.latitude, userLatLng.longitude,
                center.latitude, center.longitude,
                distance
            )
            if (distance[0] <= radiusInMeters && !isQuizFragmentShown) {
                showQuizFragment()
            }
        }
    }

    private var isInsidePolygonToastShown = false
    private fun checkIfInsidePolygon(userLatLng: LatLng) {
        // 주어진 polygonCoordinates 내에 들어왔을 때 한 번만 Toast 표시
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
     * 사용자 위치와 타겟 위치 간의 거리를 계산하여 bottom sheet의 TextView에 표시
     */
    private fun updateDistanceDisplay(userLatLng: LatLng? = null) {
        val currentLocation = userLatLng ?: userMarker?.position
        val targetLocation = targetMarker?.position
        if (currentLocation != null && targetLocation != null) {
            val results = FloatArray(1)
            Location.distanceBetween(
                currentLocation.latitude, currentLocation.longitude,
                targetLocation.latitude, targetLocation.longitude,
                results
            )
            val distanceMeters = results[0]
            val distanceText = if (distanceMeters < 1000) {
                String.format("Distance: %.0f m", distanceMeters)
            } else {
                String.format("Distance: %.2f km", distanceMeters / 1000)
            }
            bottomSheetTextView.text = distanceText
        } else {
            bottomSheetTextView.text = "Distance: N/A"
        }
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
