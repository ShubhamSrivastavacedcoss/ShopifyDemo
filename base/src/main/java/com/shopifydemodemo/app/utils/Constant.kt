package com.shopifydemodemo.app.utils

import android.R
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.shopify.buy3.HttpCachePolicy
import com.shopifydemodemo.app.productsection.models.VariantData
import java.util.concurrent.TimeUnit


object Constant {
    var ispersonalisedEnable: Boolean = false
    var previous: VariantData? = null
    var current: VariantData? = null
    var policy: HttpCachePolicy.ExpirePolicy = HttpCachePolicy.Default.CACHE_FIRST.expireAfter(5, TimeUnit.SECONDS)
    fun getProgressDialog(context: Context, msg: String): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(msg)
        progressDialog.setCancelable(false)
        return progressDialog
    }

    fun activityTransition(context: Context) {
        (context as Activity).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun logAddToWishlistEvent(contentData: String?, contentId: String?, contentType: String?, currency: String?, price: Double, context: Context) {
        val logger = AppEventsLogger.newLogger(context)
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, contentData)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType)
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency)
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, price, params)
    }

    fun logAddToCartEvent(contentData: String?, contentId: String?, contentType: String?, currency: String?, price: Double, context: Context) {
        val logger = AppEventsLogger.newLogger(context)
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, contentData)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType)
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency)
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, price, params)
    }

    fun logCompleteRegistrationEvent(registrationMethod: String?, context: Context) {
        val logger = AppEventsLogger.newLogger(context)
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, registrationMethod)
        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, params)
    }

    fun logViewContentEvent(contentType: String?, contentData: String?, contentId: String?, currency: String?, price: Double, context: Context) {
        val logger = AppEventsLogger.newLogger(context)
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, contentData)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId)
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency)
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, price, params)
    }

    fun checkInternetConnection(context: Context): Boolean {
        val connectivity = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity == null) {
            return false
        } else {
            val info = connectivity.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
