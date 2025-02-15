package com.ssafy.mbg.ui.map

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.ssafy.mbg.R
import com.ssafy.mbg.di.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class PhotoMissionFragment : DialogFragment() {

    companion object {
        /**
         * @param codeId      미션 코드 (예: "M003")
         * @param positionName 미션 장소 이름
         * @param placeName   문화유산 장소명
         * @param missionId   해당 미션의 ID (MissionExplainFragment에서 전달)
         */
        fun newInstance(codeId: String, positionName: String, placeName: String, missionId: Int): PhotoMissionFragment {
            val fragment = PhotoMissionFragment()
            val args = Bundle().apply {
                putString("codeId", codeId)
                putString("positionName", positionName)
                putString("placeName", placeName)
                putInt("missionId", missionId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    // Dagger-Hilt로 주입받음 (AppModule 등에서 OkHttpClient, UserPreferences 제공)
    @javax.inject.Inject
    lateinit var client: OkHttpClient

    @javax.inject.Inject
    lateinit var userPreferences: UserPreferences

    private var selectedImageUri: Uri? = null
    private lateinit var imageView: ImageView
    private lateinit var btnSelectPhoto: Button
    private lateinit var btnUploadPhoto: Button
    private lateinit var progressBar: ProgressBar

    // ActivityResult API를 통해 갤러리에서 이미지 선택
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // RoundedCornerDialog 스타일 적용 (styles.xml에 정의되어 있어야 함)
        setStyle(STYLE_NORMAL, R.style.RoundedCornerDialog)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // fragment_photo_mission_upload.xml 레이아웃 사용
        return inflater.inflate(R.layout.fragment_photo_mission_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageView = view.findViewById(R.id.imageViewUpload)
        btnSelectPhoto = view.findViewById(R.id.btnSelectPhoto)
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto)
        progressBar = view.findViewById(R.id.uploadProgressBar)
        progressBar.visibility = View.GONE

        btnSelectPhoto.setOnClickListener {
            // "image/*" MIME 타입으로 갤러리에서 사진 선택
            getContent.launch("image/*")
        }

        btnUploadPhoto.setOnClickListener {
            uploadPhoto()
        }
    }

    private fun uploadPhoto() {
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 업로드 진행중 UI 처리
        progressBar.visibility = View.VISIBLE
        btnUploadPhoto.isEnabled = false
        btnSelectPhoto.isEnabled = false

        // MissionExplainFragment에서 전달받은 missionId (없으면 에러 처리)
        val missionId = arguments?.getInt("missionId") ?: run {
            Toast.makeText(requireContext(), "미션 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        // UserPreferences에서 roomId와 groupNo를 가져옴
        val roomId = userPreferences.roomId
        val groupNo = userPreferences.groupNo

        if (roomId == null) {
            Toast.makeText(requireContext(), "필수 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        // API URL 구성: roomId와 missionId를 경로 변수로 사용
        val url = "https://i12d106.p.ssafy.io/api/missions/photo/$roomId/$missionId"

        // 선택한 이미지 Uri를 임시 파일로 변환
        val file = uriToFile(selectedImageUri!!, requireContext())
        if (file == null) {
            Toast.makeText(requireContext(), "파일을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            btnUploadPhoto.isEnabled = true
            btnSelectPhoto.isEnabled = true
            return
        }

        // 이미지의 MIME 타입을 가져옴 (없으면 기본 image/jpeg)
        val mimeType = requireContext().contentResolver.getType(selectedImageUri!!) ?: "image/jpeg"
        val fileRequestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

        // Multipart/form-data 구성 (roomId, missionId, groupNo, photo)
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("roomId", roomId.toString())
            .addFormDataPart("missionId", missionId.toString())
            .addFormDataPart("groupNo", groupNo.toString())
            .addFormDataPart("photo", file.name, fileRequestBody)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(multipartBody)
            .addHeader("accept", "*/*")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("PhotoMission", "사진 업로드 실패", e)
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    btnUploadPhoto.isEnabled = true
                    btnSelectPhoto.isEnabled = true
                    Toast.makeText(requireContext(), "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    btnUploadPhoto.isEnabled = true
                    btnSelectPhoto.isEnabled = true
                }
                if (!response.isSuccessful) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "roomID missionId: ${roomId} ${missionId} ", Toast.LENGTH_SHORT).show()
                        Toast.makeText(requireContext(), "서버 오류: ${response.code}", Toast.LENGTH_SHORT).show()
                        Toast.makeText(requireContext(), "서버 오류: ${url}", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                response.body?.string()?.let { responseBody ->
                    try {
                        val gson = Gson()
                        val photoResponse = gson.fromJson(responseBody, PhotoMissionResponse::class.java)
                        // 응답에서 pictureUrl만 사용 (사진이 없으면 다른 이미지로 처리하도록 다른 코드에서 구현)
                        requireActivity().runOnUiThread {
                            val resultFragment = PhotoMissionResultFragment.newInstance(photoResponse.pictureUrl)
                            resultFragment.show(parentFragmentManager, "PhotoMissionResult")
                            dismiss()
                        }
                    } catch (e: Exception) {
                        Log.e("PhotoMission", "응답 파싱 오류", e)
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "응답 파싱 오류", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    // Uri → File 변환 (임시 파일로 저장)
    private fun uriToFile(uri: Uri, context: Context): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = getFileName(uri, context) ?: "upload_image"
            val tempFile = File.createTempFile("upload_", fileName, context.cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            tempFile
        } catch (e: Exception) {
            Log.e("PhotoMission", "uriToFile 에러", e)
            null
        }
    }

    // Uri로부터 파일 이름 추출
    private fun getFileName(uri: Uri, context: Context): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex != -1 && it.moveToFirst()) {
                    result = it.getString(columnIndex)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 너비를 화면 너비의 85%로 설정
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

// API 응답 JSON을 매핑할 데이터 클래스
data class PhotoMissionResponse(
    val pictureId: Int,
    val roomId: Int,
    val userId: Int,
    val missionId: Int,
    val pictureUrl: String,
    val completionTime: String
)
