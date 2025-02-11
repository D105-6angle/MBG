package com.ssafy.mbg.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.mbg.R
//import com.ssafy.mbg.domain.repository.DataStoreRepository
import com.ssafy.mbg.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

//    @Inject
//    lateinit var dataStoreRepository: DataStoreRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Timber.d("새 FCM 토큰: $token")

        // 필요 시 서버에 토큰 업로드
        uploadTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        CoroutineScope(Dispatchers.IO).launch {
//            val isAlarmReceive = dataStoreRepository.getAlarmReceiveMode().first()
//            if (!isAlarmReceive) {
//                return@launch
//            }

            var messageTitle = ""
            var messageContent = ""

            // 알림 페이로드가 있는 경우
            if (remoteMessage.notification != null) {
                Timber.d("FCM 메시지 수신: " + remoteMessage.data)
                val title = remoteMessage.notification?.title ?: "새 알림"
                val message = remoteMessage.notification?.body ?: "알림 내용이 없습니다."
                sendNotification(title, message)

            } else {
                // 데이터 페이로드 처리
                val title = remoteMessage.data["myTitle"] ?: "새 알림"
                val message = remoteMessage.data["myBody"] ?: "알림 내용이 없습니다."
                Timber.d("FCM 메시지 수신: title=$title, body=$message")
                sendNotification(title, message)
            }
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "fcm_channel"

        val channel = NotificationChannel(
            channelId,
            "FCM 알림",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification) // 알림 아이콘 설정
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(0, notification)
    }

    private fun uploadTokenToServer(token: String) {
        // 서버로 FCM 토큰을 전송하는 로직을 여기에 구현하세요.
        Timber.d("FCM 토큰 서버 전송: $token")
    }
}
