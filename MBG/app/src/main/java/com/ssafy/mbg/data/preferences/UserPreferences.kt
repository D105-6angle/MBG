package com.ssafy.mbg.data.preferences

import android.content.Context

class UserPreferences(private val context: Context ){
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var userId: String?
        get() = prefs.getString("user_id", null)
        set(value) = prefs.edit().putString("user_id", value).apply()
}