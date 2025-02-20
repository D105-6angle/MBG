package com.ssafy.tmbg.di

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

    fun saveToken(accessToken : String, refreshToken : String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getAcessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken() : String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun clearToken() {
        prefs.edit().clear().apply()
    }

    fun saveProviderId(providerId: String) {
        prefs.edit()
            .putString(KEY_PROVIDER_ID, providerId)
            .apply()
    }

    fun clearAll() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_PROVIDER_ID)
            .apply()
    }

    fun getProviderId(): String? = prefs.getString(KEY_PROVIDER_ID, null)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_PROVIDER_ID = "provider_id"
    }
}