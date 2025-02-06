package com.ssafy.mbg.data.manger

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerTokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "server_token_prefs",
        Context.MODE_PRIVATE
    )

    fun saveToken(token : String) {
        prefs.edit()
            .putString(KEY_SERVER_TOKEN, token)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_SERVER_TOKEN, null)

    fun clearToken() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_SERVER_TOKEN = "server_token"
    }
}