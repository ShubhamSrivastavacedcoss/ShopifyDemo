package com.shopifydemodemo.app.utils

import android.util.Log
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.repositories.Repository
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import javax.inject.Inject

class Urls {
    @Inject
    lateinit var repository: Repository
    var app: MyApplication

    constructor(app: MyApplication) {
        this.app = app
        app.mageNativeAppComponent!!.doURlInjection(this)
    }

    companion object Data {
        const val BASE_URL: String = "https://shopifymobileapp.cedcommerce.com/"//put your base url here
        const val PERSONALISED: String = "https://recommendations.loopclub.io/api/v1/"//put your base url here
        const val MENU: String = "shopifymobile/shopifyapi/getcategorymenus/"//put your end point here
        const val SETORDER: String = "shopifymobile/shopifyapi/setorder"
        const val SETDEVICES: String = "shopifymobile/shopifyapi/setdevices"
        const val RECOMMENDATION: String = "recommendations/"
        const val HEADER: String = "Domain-Name: douban"
        const val HOMEPAGE: String = "shopifymobile/shopifyapi/homepagedata"
        const val CLIENT: String = "magenative"
        const val TOKEN: String = "a2ds21R!3rT#R@R23r@#3f3ef"
        const val MulipassSecret: String = "1f4237c87f31090e5763feaa34962b72"
        const val SIZECHART: String = "https://app.kiwisizing.com/size"
        const val JUDGEME_BASEURL: String = "https://judge.me/api/v1/"
        const val JUDGEME_REVIEWCOUNT: String = JUDGEME_BASEURL + "reviews/count/"
        const val JUDGEME_REVIEWINDEX: String = JUDGEME_BASEURL + "reviews"
        const val JUDGEME_REVIEWCREATE: String = JUDGEME_BASEURL + "reviews"
        const val JUDGEME_GETPRODUCTID: String = JUDGEME_BASEURL + "products/"
        var JUDGEME_APITOKEN: String = "R8kqByFI_qHiHHQj6ZV1yWCYveQ"
        const val ALIREVIEW_BASEURL: String = "https://alireviews.fireapps.io/"
        const val ALIREVIEW_INSTALLSTATUS: String = ALIREVIEW_BASEURL + "api/shops/magenative.myshopify.com"
        const val ALIREVIEW_PRODUCT: String = ALIREVIEW_BASEURL + "comment/get_review"
        const val YOTPOBASE_URL = "https://loyalty.yotpo.com/api/v2/"
        const val GETREWARDS = YOTPOBASE_URL + "redemption_options"
        const val REDEEMPOINTS = YOTPOBASE_URL + "redemptions"
        const val EARNREWARD = YOTPOBASE_URL + "campaigns"
        const val MYREWARDS = YOTPOBASE_URL + "customers"
        const val SENDREFERRAL = YOTPOBASE_URL + "referral/share"
        const val VALIDATE_DELIVERY: String = "shopifymobile/zapietstorepickupapi/validatedeliverynpickup"
        const val LOCAL_DELIVERY: String = "shopifymobile/zapietstorepickupapi/getdeliverynpickup"
        const val LOCAL_DELIVERYY: String = "shopifymobilenew/zapietstorepickupapi/getdeliverynpickup"
        const val DeliveryStatus: String = "https://shopifymobileapp.cedcommerce.com/shopifymobile/zapietstorepickupapi/installedstatus?"
        var XGUID = "oyeoRDurwhul3WK-zN5ScA"
        var X_API_KEY = "FCCVWdq07tgQCkq8Bw8ctQtt"
        var fbusername = "MageNative"
        var whatsappnumber = "+916393417500"
        const val MENUCOLLECTION: String = "http://shopifymobileapp.cedcommerce.com/shopifymobile/shopifyapi/getcollectionproperties"
        const val FILTERTAGPRO: String ="https://shopifymobileapp.cedcommerce.com/shopifymobile/shopifyapi/getcollectionproductsbytags"
        /************************** Yotpo Rewards Integration ***************************/

        const val YOTPOAUTHENTICATE = "https://api.yotpo.com/oauth/token"
        const val YOTPOCREATEREVIEW = "https://api.yotpo.com/v1/widget/reviews"

        /********************************************************************************/
        /********************************** DICOUNTCODE *********************************/

        const val DISCOUNTCODEAPPLY: String = "https://shopifymobileapp.cedcommerce.com/shopifymobilenew/discountpaneldataapi/getdiscountcodes/"

        /********************************************************************************/
    }

    val shopdomain: String
        get() {
            var domain = "random-booksin.myshopify.com" //magenative-store.myshopify.com
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    if (repository.getPreviewData().size > 0) {
                        domain = repository.getPreviewData().get(0).shopurl!!
                    }
                    domain
                }
                val future = executor.submit(callable)
                domain = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.i("MageNative", "domain" + domain)
            return domain
        }
    val mid: String
        get() {
            var domain = "9752"//"9630" // 3937
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    if (repository.getPreviewData().size > 0) {
                        domain = repository.getPreviewData().get(0).mid!!
                    }
                    domain
                }
                val future = executor.submit(callable)
                domain = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.i("MageNative", "domain" + domain)
            return domain
        }
    val apikey: String
        get() {
            var key = "3ac305339aee0fea6968d9839bc46786" //63893d2330e639632e2eab540e9d2d75
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    if (repository.getPreviewData().size > 0) {
                        key = repository.getPreviewData().get(0).apikey!!
                    }
                    key
                }
                val future = executor.submit(callable)
                key = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.i("MageNative", "domain" + key)
            return key
        }
}
