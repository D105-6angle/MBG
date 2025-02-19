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
import androidx.core.content.FileProvider
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

    @javax.inject.Inject
    lateinit var client: OkHttpClient

    @javax.inject.Inject
    lateinit var userPreferences: UserPreferences

    private var selectedImageUri: Uri? = null
    private var capturedImageUri: Uri? = null
    private lateinit var imageView: ImageView
    private lateinit var btnSelectPhoto: Button
    private lateinit var btnCapturePhoto: Button
    private lateinit var btnUploadPhoto: Button
    private lateinit var progressBar: ProgressBar

    // 갤러리에서 사진 선택 ActivityResultLauncher
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imageView.setImageURI(it)
        }
    }

    // 카메라 촬영 ActivityResultLauncher (TakePicture는 결과를 저장할 URI가 필요)
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            imageView.setImageURI(capturedImageUri)
            selectedImageUri = capturedImageUri
        } else {
            Toast.makeText(requireContext(), "사진 촬영에 실패했습니다.", Toast.LENGTH_SHORT).show()
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
        btnCapturePhoto = view.findViewById(R.id.btnCapturePhoto)
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto)
        progressBar = view.findViewById(R.id.uploadProgressBar)
        progressBar.visibility = View.GONE

        btnSelectPhoto.setOnClickListener {
            // 갤러리에서 사진 선택 ("image/*" MIME 타입)
            getContent.launch("image/*")
        }

        btnCapturePhoto.setOnClickListener {
            // 카메라로 사진 촬영하기 위해 임시 파일 생성 후 FileProvider로 URI 획득
            val tempFile = File.createTempFile("captured_", ".jpg", requireContext().cacheDir)
            // 여기서 "com.ssafy.mbg.fileprovider" 는 AndroidManifest.xml에 선언한 authorities와 일치해야 합니다.
            capturedImageUri = FileProvider.getUriForFile(requireContext(), "com.ssafy.mbg.fileprovider", tempFile)
            takePictureLauncher.launch(capturedImageUri)
        }

        btnUploadPhoto.setOnClickListener {
            uploadPhoto()
        }
    }

    private fun uploadPhoto() {
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "사진을 선택하거나 촬영해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 업로드 진행 UI 처리
        progressBar.visibility = View.VISIBLE
        btnUploadPhoto.isEnabled = false
        btnSelectPhoto.isEnabled = false
        btnCapturePhoto.isEnabled = false

        val missionId = arguments?.getInt("missionId") ?: run {
            Toast.makeText(requireContext(), "미션 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        val roomId = userPreferences.roomId
        val groupNo = userPreferences.groupNo

        if (roomId == null) {
            Toast.makeText(requireContext(), "필수 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        // API URL 구성
        val url = "https://i12d106.p.ssafy.io/api/missions/photo/$roomId/$missionId"
        val file = uriToFile(selectedImageUri!!, requireContext())
        if (file == null) {
            Toast.makeText(requireContext(), "파일을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            btnUploadPhoto.isEnabled = true
            btnSelectPhoto.isEnabled = true
            btnCapturePhoto.isEnabled = true
            return
        }

        val mimeType = requireContext().contentResolver.getType(selectedImageUri!!) ?: "image/jpeg"
        // 명시적으로 타입 지정하여 컴파일러 오류 해결
        val mediaType = mimeType.toMediaTypeOrNull() ?: "image/jpeg".toMediaTypeOrNull()
        val fileRequestBody: RequestBody = file.asRequestBody(mediaType)
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
                    btnCapturePhoto.isEnabled = true
                    Toast.makeText(requireContext(), "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    progressBar.visibility = View.GONE
                    btnUploadPhoto.isEnabled = true
                    btnSelectPhoto.isEnabled = true
                    btnCapturePhoto.isEnabled = true
                }
                if (!response.isSuccessful) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "서버 오류: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                response.body?.string()?.let { responseBody ->
                    try {
                        val gson = Gson()
                        val photoResponse = gson.fromJson(responseBody, PhotoMissionResponse::class.java)
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

    // Uri → File 변환 (임시 파일 생성)
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
        // 다이얼로그 너비를 화면 너비의 85%로 설정
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

// PhotoMissionResponse 데이터 클래스 (API 응답에 맞게 수정)
data class PhotoMissionResponse(
    val pictureId: Int,
    val roomId: Int,
    val userId: Int,
    val missionId: Int,
    val pictureUrl: String,
    val completionTime: String
)
