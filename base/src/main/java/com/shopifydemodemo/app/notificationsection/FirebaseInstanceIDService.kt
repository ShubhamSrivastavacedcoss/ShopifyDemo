package com.shopifydemodemo.app.notificationsection

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseInstanceIDService : FirebaseMessagingService() {
    override fun onNewToken(refreshedToken: String) {
        super.onNewToken(refreshedToken)
        Log.d("NEW_TOKEN", refreshedToken)
    }
}
