package com.shopifydemodemo.app.jobservicessection

import android.app.ActivityManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.cartsection.activities.CartList
import com.shopifydemodemo.app.notificationsection.NotificationUtils
import com.shopifydemodemo.app.repositories.Repository
import javax.inject.Inject

class JobScheduler : JobService() {
    @Inject
    lateinit var repository: Repository

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        (application as MyApplication).mageNativeAppComponent!!.doServiceInjection(this)
        val runnable = Runnable {
            if (repository!!.allCartItems.size > 0) {
                if (isAppIsInBackground(applicationContext)) {
                    showCartNotification()
                }
                jobFinished(jobParameters, false)
            } else {
                Log.i("MageNative", "No Cart")
            }
        }
        Thread(runnable).start()
        return true
    }

    private fun showCartNotification() {
        try {
            var tittle = ""
            if (repository!!.isLogin) {
                tittle = applicationContext.resources.getString(R.string.heyuser) + " " + repository!!.allUserData[0].firstname + " " + repository!!.allUserData[0].lastname
            } else {
                tittle = resources.getString(R.string.app_name)
            }
            val intent = Intent(applicationContext, CartList::class.java)
            val notificationUtils = NotificationUtils(applicationContext)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            notificationUtils.showNotificationMessage(tittle, resources.getString(R.string.somethingleftinyourcart), intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        Log.i("CartValues2", "cancel")
        jobFinished(jobParameters, true)
        return false
    }

    companion object {
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
    }
}
