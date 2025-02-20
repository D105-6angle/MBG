package com.ssafy.tmbg.ui.team

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tmbg.data.team.dao.TeamRequest
import com.ssafy.tmbg.data.team.repository.TeamRepository
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.ssafy.tmbg.data.team.dao.Team
import kotlinx.coroutines.launch
import com.ssafy.tmbg.data.team.dao.GroupDetailResponse
import android.content.Intent
import android.util.Log
import com.ssafy.tmbg.ui.SharedViewModel

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val repository: TeamRepository,
) : ViewModel() {

    private val _team = MutableLiveData<Team?>()
    val team: LiveData<Team?> = _team

    private val _hasTeam = MutableLiveData<Boolean>()
    val hasTeam: LiveData<Boolean> = _hasTeam

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _groupDetail = MutableLiveData<GroupDetailResponse>()
    val groupDetail: LiveData<GroupDetailResponse> = _groupDetail

    // SharedViewModel의 roomId 사용


    suspend fun createTeam(teamRequest: TeamRequest): Result<Int> {
        return try {
            val response = repository.createTeam(teamRequest)
            if (response.isSuccessful) {
                response.body()?.let { teamCreateResponse ->
                    val newRoomId = teamCreateResponse.roomId.toInt()
                    getTeam(newRoomId)
                    Result.success(newRoomId)
                } ?: run {
                    _error.value = "팀 생성 응답이 비어있습니다"
                    Result.failure(Exception("팀 생성 응답이 비어있습니다"))
                }
            } else {
                _error.value = "팀 생성에 실패했습니다 (${response.code()})"
                Result.failure(Exception("팀 생성 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            _error.value = "네트워크 오류: ${e.message}"
            Result.failure(e)
        }
    }

    fun shareInviteCode(context: Context) {
        team.value?.let { team ->
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, team.inviteCode)
            }

            try {
                context.startActivity(Intent.createChooser(shareIntent, "초대 코드 공유하기"))
            } catch (e: Exception) {
                Toast.makeText(context, "공유하기를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getTeam(roomId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getTeam(roomId)

                if (response.isSuccessful) {
                    response.body()?.let { teamResponse ->
                        _team.value = teamResponse
                    } ?: run {
                        _error.value = "팀 정보가 비어있습니다"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "팀 정보 조회에 실패했습니다 (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 에러가 발생했습니다"
            }
        }
    }

    fun getGroupDetail(roomId: Int, groupNo: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getGroupDetail(roomId, groupNo)
                if (response.isSuccessful) {
                    response.body()?.let { detail ->
                        _groupDetail.value = detail
                    }
                } else {
                    _error.value = "그룹 상세 정보를 불러오는데 실패했습니다."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다."
            }
        }
    }

    fun addGroup(roomId: Int) {
        viewModelScope.launch {
            try {
                Log.d("TeamViewModel", "addGroup 호출됨 - roomId: $roomId")
                val response = repository.addGroup(roomId)
                Log.d("TeamViewModel", "addGroup API 응답: $response")

                if (response.isSuccessful) {
                    Log.d("TeamViewModel", "그룹 추가 성공")
                    getTeam(roomId)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("TeamViewModel", "그룹 추가 실패 - HTTP ${response.code()}, Error: $errorBody")
                    _error.value = "그룹 추가에 실패했습니다 (${response.code()})"
                }
            } catch (e: Exception) {
                Log.e("TeamViewModel", "그룹 추가 에러", e)
                _error.value = "네트워크 오류: ${e.message}"
            }
        }
    }

//    fun deleteMember(roomId: Int, groupNo: Int, userId: Long) {
//        viewModelScope.launch {
//            try {
//                Log.d(
//                    "TeamViewModel",
//                    "deleteMember 호출 - roomId: $roomId, groupNo: $groupNo, userId: $userId"
//                )
//                val response = repository.deleteMember(roomId, groupNo, userId)
//                Log.d("TeamViewModel", "deleteMember 응답: $response")
//
//                if (response.isSuccessful) {
//                    Log.d("TeamViewModel", "멤버 삭제 성공")
//                    getGroupDetail(roomId, groupNo)
//                } else {
//                    val errorBody = response.errorBody()?.string()
//                    Log.e("TeamViewModel", "멤버 삭제 실패 - HTTP ${response.code()}, Error: $errorBody")
//                    _error.value = "멤버 삭제에 실패했습니다 (${response.code()})"
//                }
//            } catch (e: Exception) {
//                Log.e("TeamViewModel", "deleteMember 에러", e)
//                _error.value = "네트워크 오류: ${e.message}"
//            }
//        }
//    }
}