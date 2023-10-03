package com.example.chatapp

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val name = "채팅 알림"
        val descriptionText = "채팅 알림입니다"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(   //notification 채널 만듬
            // https://developer.android.com/develop/ui/views/notifications/channels

            getString(R.string.default_notification_channel_id), //앱 내에서 채널을 여러개 만들수 있으므로 구분할 수 있는 id
            name,
            importance
        )
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager // notificationManager 에 연결
        notificationManager.createNotificationChannel(mChannel)

        val body = message.notification?.body ?: ""  //메시지를 보낼때 body에 알림에 보여줄 텍스트메세지
        val notificationBuilder = NotificationCompat.Builder(
            applicationContext,
            getString(R.string.default_notification_channel_id)
        )
            .setSmallIcon(R.drawable.baseline_chat_24) //아이콘 보여줌
            .setContentTitle(getString(R.string.app_name))
            .setContentText(body)

        notificationManager.notify(0,notificationBuilder.build())



    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}