package com.mkitsimple.counterboredom.ui

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mkitsimple.counterboredom.utils.toast

// This will recieve the incoming messages of notifications
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var helper: NotificationHelper? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            //Log.d(TAG, "Message data payload: " + remoteMessage.data)
            toast(remoteMessage.data.toString())
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
            toast(remoteMessage.notification!!.body.toString())
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}