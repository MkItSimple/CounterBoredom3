package com.mkitsimple.counterboredom.ui

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// This will recieve the incoming messages of notifications
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var helper: NotificationHelper? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.notification != null){
            val title: String = remoteMessage.notification?.title!!
            val body: String = remoteMessage.notification?.body!!
            helper?.displayNotification(applicationContext, title, body)
        }
    }
}