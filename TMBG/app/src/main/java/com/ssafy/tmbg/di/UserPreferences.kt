package com.ssafy.tmbg.di

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
}