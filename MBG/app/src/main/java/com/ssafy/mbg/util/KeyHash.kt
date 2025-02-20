package com.ssafy.mbg.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.security.MessageDigest

object KeyHashUtil {
    fun getKeyHash(context : Context) : String {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.d("KeyHash", keyHash)
                return keyHash
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "키 해시 가져오기 실패", e)
        }
        return ""
    }
}