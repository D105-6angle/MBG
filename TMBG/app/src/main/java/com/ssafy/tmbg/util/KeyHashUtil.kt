package com.ssafy.tmbg.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import android.os.Build


object KeyHashUtil {
    fun getKeyHash(context: Context): String {
        try {
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                info.signatures
            }

            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.d("KeyHash", "Debug KeyHash: $keyHash")
                return keyHash
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "키 해시 가져오기 실패", e)
        }
        return ""
    }

    fun getReleaseKeyHash(context: Context): String {
        try {
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                info.signatures
            }

            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.d("KeyHash", "Release KeyHash: $keyHash")
                return keyHash
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "릴리즈 키 해시 가져오기 실패", e)
        }
        return ""
    }

    fun getAllKeyHashes(context: Context) {
        val debugKeyHash = getKeyHash(context)
        val releaseKeyHash = getReleaseKeyHash(context)

        Log.d("KeyHash", "Debug KeyHash: $debugKeyHash")
        Log.d("KeyHash", "Release KeyHash: $releaseKeyHash")
    }
}