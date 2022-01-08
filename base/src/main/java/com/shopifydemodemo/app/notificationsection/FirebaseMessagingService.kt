package com.shopifydemodemo.app.notificationsection

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.basesection.activities.Splash
import com.shopifydemodemo.app.utils.Urls
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.Objects

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {
    private var notificationUtils: NotificationUtils? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            Log.d(TAG, "From: " + remoteMessage.from!!)
            Log.i("notification_test", "" + remoteMessage.data.toString())
            val `object` = JSONObject(remoteMessage.data.toString())
            val data = `object`.getJSONObject("data")
            val title = data.getString("title")
            val merchant_id = data.getString("merchant_id")
            val mesg = data.getString("message")
            val imageUri = data.getString("image")
            val link_type = data.getJSONObject("payload").getString("link_type")
            val link_id = data.getJSONObject("payload").getString("link_id")
            var resultIntent: Intent? = null
            when (link_type) {
                "product" -> {
                    val product_id = "gid://shopify/Product/$link_id"
                    resultIntent = Intent(applicationContext, Splash::class.java)
                    resultIntent.putExtra("ID", getBase64Encode(product_id))
                    resultIntent.putExtra("type", "product")
                }
                "collection" -> {
                    val s1 = "gid://shopify/Collection/$link_id"
                    resultIntent = Intent(applicationContext, Splash::class.java)
                    resultIntent.putExtra("ID", getBase64Encode(s1))
                    resultIntent.putExtra("tittle", title)
                    resultIntent.putExtra("type", "collection")
                }
                "web_address" -> {
                    resultIntent = Intent(applicationContext, Splash::class.java)
                    resultIntent.putExtra("link", link_id)
                    resultIntent.putExtra("name", " ")
                    resultIntent.putExtra("type", "weblink")
                }
            }
            if (merchant_id.equals(Urls((application as MyApplication))!!.mid)) {
                if (TextUtils.isEmpty(imageUri)) {
                    showNotificationMessage(applicationContext, title, mesg, Objects.requireNonNull<Intent>(resultIntent))
                } else {
                    showNotificationMessageWithBigImage(applicationContext, title, mesg, Objects.requireNonNull<Intent>(resultIntent), imageUri)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showNotificationMessage(context: Context, title: String, message: String, intent: Intent) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils!!.showNotificationMessage(title, message, intent)
    }

    private fun showNotificationMessageWithBigImage(context: Context, title: String, message: String, intent: Intent, imageUrl: String) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils!!.showNotificationMessage(title, message, intent, imageUrl)
    }

    fun getBase64Encode(id: String): String {
        var id = id
        val data = Base64.encode(id.toByteArray(), Base64.DEFAULT)
        try {
            id = String(data, Charset.defaultCharset()).trim { it <= ' ' }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return id
    }

    companion object {
        private val TAG = "FirebaseMessageService"
    }
}