package com.shopifydemodemo.app.sharedprefsection

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("StaticFieldLeak")
object MagePrefs {
    private var context: Context? = null
    private var sharedPreference: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private val HOMEDATA = "home_data"
    private val LANGUAGE = "language"
    private val PREF_NAME = "MagenativeShopify"
    private val HOME_PRODUCTS = "home_products"
    private val CART_AMOUNT = "cart_amount"
    private val APPCURRENCY = "currency"
    private const val CUSTOMERID = "customerId"
    private const val ACCESSTOKEN = "accesstoken"
    private const val CUSTOMEREMAIL = "customerEmail"
    private const val MAINTENANCE = "maintenancemode"
    private const val FIRSTNAME = "firstname"
    private const val LASTNAME = "lastname"
    private const val COUPONCODE = "coupon_code"

    fun getInstance(context: Context) {
        this.context = context
        sharedPreference = context?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreference?.edit()
    }

    fun setHomepageData(homepage_date: String?) {
        editor?.putString(HOMEDATA, homepage_date)
        editor?.commit()
    }

    fun getHomepageData(): String? {
        return sharedPreference?.getString(HOMEDATA, null)
    }

    fun setLanguage(language: String) {
        editor?.putString(LANGUAGE, language)
        editor?.commit()
    }


    fun getLanguage(): String? {
        return sharedPreference?.getString(LANGUAGE, "en")
    }

    fun setGrandTotal(grand_total: String) {
        editor?.putString(CART_AMOUNT, grand_total)
        editor?.commit()
    }

    fun getGrandTotal(): String? {
        return sharedPreference?.getString(CART_AMOUNT, null)
    }

    fun setCurrency(currency: String) {
        editor?.putString(APPCURRENCY, currency)
        editor?.commit()
    }

    fun getCurrency(): String? {
        return sharedPreference?.getString(APPCURRENCY, null)
    }

    fun clearHomeData() {
        editor?.remove(HOMEDATA)
        editor?.commit()
    }

    fun setCustomerId(customerID: String) {
        editor?.putString(CUSTOMERID, customerID)
        editor?.commit()
    }

    fun getCustomerID(): String? {
        return sharedPreference?.getString(CUSTOMERID, null)
    }

    fun setCustomerFirstName(firstname: String) {
        editor?.putString(FIRSTNAME, firstname)
        editor?.commit()
    }

    fun getCustomerFirstName(): String? {
        return sharedPreference?.getString(FIRSTNAME, null)
    }

    fun setCustomerLastName(lastname: String) {
        editor?.putString(LASTNAME, lastname)
        editor?.commit()
    }

    fun getCustomerLastName(): String? {
        return sharedPreference?.getString(LASTNAME, null)
    }

    fun setCustomerEmail(customerEmail: String) {
        editor?.putString(CUSTOMEREMAIL, customerEmail)
        editor?.commit()
    }

    fun getaccessToken(): String? {
        return sharedPreference?.getString(ACCESSTOKEN, null)
    }

    fun saveaccessToken(accesstoken: String) {
        editor?.putString(ACCESSTOKEN, accesstoken)
        editor?.commit()
    }

    fun getCustomerEmail(): String? {
        return sharedPreference?.getString(CUSTOMEREMAIL, null)
    }

    fun clearUserData() {
        editor?.remove(CUSTOMERID)
        editor?.remove(CUSTOMEREMAIL)
        editor?.commit()
    }

    fun setMaintenanceMode(maintenance: Boolean) {
        editor?.putBoolean(MAINTENANCE, maintenance)
        editor?.commit()
    }

    fun getMaintenanceMode(): Boolean? {
        return sharedPreference?.getBoolean(MAINTENANCE, false)
    }

    fun setCouponCode(couponCode: String) {
        editor?.putString(COUPONCODE, couponCode)
        editor?.commit()
    }

    fun getCouponCode(): String? {
        return sharedPreference?.getString(COUPONCODE, null)
    }

    fun clearCouponCode() {
        editor?.remove(COUPONCODE)
        editor?.commit()
    }
}