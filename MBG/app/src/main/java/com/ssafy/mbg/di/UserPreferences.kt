package com.ssafy.mbg.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class UserPreferences(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val LOCATION_KEY = "location"
    }

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _locationFlow = MutableStateFlow("")
    val locationFlow: StateFlow<String> = _locationFlow.asStateFlow()

    var userId: Long?
        get() = prefs.getLong("user_id", -1).let { if (it == -1L) null else it }
        set(value) = prefs.edit().apply {
            if (value != null) {
                putLong("user_id", value)
            } else {
                remove("user_id")
            }
        }.apply()

    var location: String
        get() = prefs.getString(LOCATION_KEY, "") ?: ""
        set(value) {
            prefs.edit().putString(LOCATION_KEY, value).apply()
            _locationFlow.value = value
        }

    var roomId: Long?
        get() = prefs.getLong("room_id", -1).let { if (it == -1L) null else it }
        set(value) = prefs.edit().apply {
            if (value != null) {
                putLong("room_id", value)
            } else {
                remove("room_id")
            }
        }.apply()

    var groupNo: Int
        get() = prefs.getInt("group_no", 0)
        set(value) = prefs.edit().putInt("group_no", value).apply()
    // J001 조장 , J002 조원 -> 인증샷 미션 조장만 활성화
    var codeId: String
        get() = prefs.getString("code_id", "") ?: ""
        set(value) = prefs.edit().putString("code_id", value).apply()
}
