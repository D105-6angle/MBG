package com.ssafy.tmbg.ui.team

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tmbg.data.team.dao.TeamRequest
import com.ssafy.tmbg.data.team.repository.TeamRepository
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.ssafy.tmbg.data.team.dao.Team
import kotlinx.coroutines.launch
import android.util.Log


@HiltViewModel
class TeamViewModel @Inject constructor(
    private val repository: TeamRepository
) : ViewModel() {
    
    private val _team = MutableLiveData<Team>()
    val team: LiveData<Team> = _team

    private val _hasTeam = MutableLiveData<Boolean>()
    val hasTeam: LiveData<Boolean> = _hasTeam

    private val _roomId = MutableLiveData<Int>()
    val roomId: LiveData<Int> = _roomId

    // 에러 메시지를 위한 LiveData 추가
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        _hasTeam.value = false
        _roomId.value = -1
        _error.value = ""
    }

    fun createTeam(teamRequest: TeamRequest) {
        viewModelScope.launch {
            Log.d("TeamViewModel", "팀 생성 시작 - 요청 데이터: $teamRequest")
            try {
                val response = repository.createTeam(teamRequest)
                Log.d("TeamViewModel", "createTeam 응답 코드: ${response.code()}")
                
                if (response.isSuccessful) {
                    response.body()?.let { teamCreateResponse ->
                        val roomId = teamCreateResponse.roomId.toInt()
                        _roomId.value = roomId
                        Log.d("TeamViewModel", "팀 생성 성공 - roomId: $roomId")
                        Log.d("TeamViewModel", "getTeam 호출 직전")
                        getTeam(roomId)
                        Log.d("TeamViewModel", "getTeam 호출 직후")
                    } ?: run {
                        Log.e("TeamViewModel", "팀 생성 성공했지만 응답 바디가 null입니다")
                        _error.value = "팀 생성 응답이 비어있습니다"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("TeamViewModel", "팀 생성 실패 - HTTP 에러: $errorBody")
                    _error.value = "팀 생성에 실패했습니다 (${response.code()})"
                }
            } catch (e: Exception) {
                Log.e("TeamViewModel", "팀 생성 실패 - 네트워크 에러: ${e.message}", e)
                _error.value = "네트워크 오류: ${e.message}"
            }
        }
    }

    fun shareInviteCode(context: Context) {
        team.value?.let { team ->
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "초대 코드: ${team.inviteCode}")
            }
            context.startActivity(Intent.createChooser(shareIntent, "초대 코드 공유"))
        }
    }

    fun getTeam(roomId: Int) {
        viewModelScope.launch {
            Log.d("TeamViewModel", "getTeam 함수 시작 - roomId: $roomId")
            try {
                Log.d("TeamViewModel", "getTeam API 호출 직전 - URL: /api/rooms/$roomId")
                val response = repository.getTeam(roomId)
                Log.d("TeamViewModel", "getTeam API 호출 직후")
                Log.d("TeamViewModel", "팀 정보 조회 응답: ${response.raw()}")
                Log.d("TeamViewModel", "응답 헤더: ${response.headers()}")
                
                if (response.isSuccessful) {
                    response.body()?.let { team ->
                        Log.d("TeamViewModel", "팀 정보 조회 성공: $team")
                        _team.value = team
                    } ?: run {
                        Log.e("TeamViewModel", "팀 정보 조회 성공했지만 응답 바디가 null입니다")
                        _error.value = "팀 정보가 비어있습니다"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("TeamViewModel", "팀 정보 조회 실패 - HTTP 에러: $errorBody")
                    Log.e("TeamViewModel", "에러 응답 코드: ${response.code()}")
                    _error.value = "팀 정보 조회에 실패했습니다 (${response.code()})"
                }
            } catch (e: Exception) {
                Log.e("TeamViewModel", "팀 정보 조회 실패 - 네트워크 에러: ${e.message}", e)
                _error.value = "네트워크 오류: ${e.message}"
            }
        }
    }
}