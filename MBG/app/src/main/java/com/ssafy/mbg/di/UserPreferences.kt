package com.ssafy.mbg.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var userId: Long?
        get() = prefs.getLong("user_id", -1).let { if (it == -1L) null else it }
        set(value) = prefs.edit().apply {
            if (value != null) {
                putLong("user_id", value)
            } else {
                remove("user_id")
            }
        }.apply()

    var location: String?
        get() = prefs.getString("location", null)
        set(value) = prefs.edit().putString("location", value).apply()

    var roomId: Long?
        get() = prefs.getLong("room_id", -1).let { if (it == -1L) null else it }
        set(value) = prefs.edit().apply {
            if (value != null) {
                putLong("room_id", value)
            } else {
                remove("room_id")
            }
        }.apply()
}