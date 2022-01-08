package com.shopifydemodemo.app.notificationsection

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.core.app.NotificationCompat
import com.shopifydemodemo.app.R
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Random

class NotificationUtils {
    private val mContext: Context
    constructor(mContext: Context) {
        this.mContext = mContext
    }

    @JvmOverloads
    fun showNotificationMessage(title: String, message: String, intent: Intent, imageUrl: String? = null) {
        try {
            if (TextUtils.isEmpty(message))
                return
            val icon = R.drawable.notification
            //val intents = arrayOf<Intent>(Intent(mContext, Splash::class.java), Intent(mContext, HomePage::class.java), intent)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val resultPendingIntent = PendingIntent.getActivity(
                    mContext,
                    0,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
            )
            val mBuilder = Notification.Builder(
                    mContext)
            val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.packageName + "/raw/notification")
            if (!TextUtils.isEmpty(imageUrl)) {
                if (imageUrl != null && imageUrl.length > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                    val bitmap = getBitmapFromURL(imageUrl)
                    if (bitmap != null) {
                        showBigNotification(bitmap, mBuilder, icon, title, message, resultPendingIntent, alarmSound)
                    } else {
                        showSmallNotification(mBuilder, icon, title, message, resultPendingIntent, alarmSound)
                    }
                }
            } else {
                showSmallNotification(mBuilder, icon, title, message, resultPendingIntent, alarmSound)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showSmallNotification(mBuilder: Notification.Builder, icon: Int, title: String, message: String, resultPendingIntent: PendingIntent, alarmSound: Uri) {
        try {
            val bigPictureStyle = NotificationCompat.BigPictureStyle()
            bigPictureStyle.setBigContentTitle(title)
            bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
            val noti = R.drawable.ic_notification
            mBuilder.setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setSmallIcon(noti)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
                    .setContentText(message)
                    .build()
            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "test"
                val description = "desc"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("CHANNEL2", name, importance)
                channel.description = description
                mBuilder.setChannelId("CHANNEL2")
                notificationManager.createNotificationChannel(channel)
            }
            val random = Random()
            val m = random.nextInt(9998 - 1000) + 1000
            notificationManager.notify(m, mBuilder.build())
        } catch (e: Exception) {
            Log.i("exception", e.toString())
        }
    }

    private fun showBigNotification(bitmap: Bitmap, mBuilder: Notification.Builder, icon: Int, title: String, message: String, resultPendingIntent: PendingIntent, alarmSound: Uri) {
        try {
            val bigPictureStyle = NotificationCompat.BigPictureStyle()
            bigPictureStyle.setBigContentTitle(title)
            bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
            bigPictureStyle.bigPicture(bitmap)
            val noti = R.drawable.ic_notification
            val drawable = BitmapDrawable(mContext.resources, bitmap)
            mBuilder.setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(Notification.BigPictureStyle().setBigContentTitle(title).bigLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon)).bigPicture(bitmap))
                    .setSmallIcon(noti)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
                    .setContentText(message)
                    .build()
            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "test"
                val description = "desc"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("CHANNEL2", name, importance)
                channel.description = description
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                mBuilder.setChannelId("CHANNEL2")
                notificationManager.createNotificationChannel(channel)
            }
            val random = Random()
            val m = random.nextInt(9998 - 1000) + 1000
            notificationManager.notify(m, mBuilder.build())
        } catch (e: Exception) {
            Log.i("exception", e.toString())
        }
    }

    fun getBitmapFromURL(strURL: String): Bitmap? {
        try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    companion object {
        private val TAG = NotificationUtils::class.java.simpleName
        /**
         * Method checks if the app is in background or not
         */
        fun isAppIsInBackground(context: Context): Boolean {
            var isInBackground = true
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                val runningProcesses = am.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (activeProcess in processInfo.pkgList) {
                            if (activeProcess == context.packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            } else {
                val taskInfo = am.getRunningTasks(1)
                val componentInfo = taskInfo[0].topActivity
                if (componentInfo!!.packageName == context.packageName) {
                    isInBackground = false
                }
            }
            return isInBackground
        }

        fun getTimeMilliSec(timeStamp: String): Long {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            try {
                val date = format.parse(timeStamp)
                return date!!.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return 0
        }
    }
}