package com.shopifydemodemo.app.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.shopify.buy3.GraphClient
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.MyApplication.Companion.context
import com.shopifydemodemo.app.dbconnection.database.AppDatabase
import com.shopifydemodemo.app.dbconnection.entities.*
import com.shopifydemodemo.app.dependecyinjection.Body
import com.shopifydemodemo.app.productsection.models.MediaModel
import com.shopifydemodemo.app.sharedprefsection.MagePrefs
import com.shopifydemodemo.app.utils.ApiCallInterface
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.Urls
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class Repository {
    private val TAG = "Repository"
    private val apiCallInterface: ApiCallInterface
    private val appdatabase: AppDatabase


    val graphClient: GraphClient
        get() {
            return GraphClient.build(
                context,
                Urls(MyApplication.context).shopdomain,
                Urls(MyApplication.context).apikey,
                {
                    httpClient = requestHeader
                    httpCache(context.cacheDir, {
                        cacheMaxSizeBytes = 1024 * 1024 * 10
                        defaultCachePolicy = Constant.policy
                        Unit
                    })
                    Unit
                },
                MagePrefs.getLanguage()
            )
        }
    internal val requestHeader: OkHttpClient
        get() {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder().build()
                chain.proceed(request)
            }
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
            return httpClient.build()
        }

    constructor(apiCallInterface: ApiCallInterface, appdatabase: AppDatabase) {
        this.apiCallInterface = apiCallInterface
        this.appdatabase = appdatabase
    }

    val localData: List<AppLocalData>
        get() = appdatabase.appLocalDataDaoDao().all
    val wishListData: List<ItemData>
        get() = appdatabase.itemDataDao.all

    val wishListDataCount: LiveData<List<ItemData>>
        get() = appdatabase.itemDataDao.wish_count

    val allCartItems: List<CartItemData>
        get() = appdatabase.cartItemDataDao.all

    val allCartItemsCount: LiveData<List<CartItemData>>
        get() = appdatabase.cartItemDataDao.cart_count

    val allUserData: List<UserLocalData>
        get() = appdatabase.appLocalDataDaoDao().allUserData
    val isLogin: Boolean
        get() = if (appdatabase.appLocalDataDaoDao().customerToken.size > 0) {
            true
        } else {
            false
        }
    val accessToken: List<CustomerTokenData>
        get() {
            var customerToken = runBlocking(Dispatchers.IO) {
                return@runBlocking appdatabase.appLocalDataDaoDao().customerToken
            }
            return customerToken
        }
    //   get() = appdatabase.appLocalDataDaoDao().customerToken

    fun getMenus(mid: String, code: String): Single<JsonElement> {
        return apiCallInterface.getMenus(mid, code)

    }

    fun getRecommendation(body: Body): Single<JsonElement> {
        Log.i("MageNative", "Cross-sell-3" + body)
        return apiCallInterface.getRecommendations(
            Urls(context).shopdomain,
            Urls.CLIENT,
            Urls.TOKEN,
            "application/json",
            body
        )
    }

    fun getHomePage(mid: String): Single<JsonElement> {
        return apiCallInterface.getHomePage(mid)
    }

    fun setDevice(
        mid: String,
        device_id: String,
        email: String,
        type: String,
        unique_id: String
    ): Single<JsonElement> {
        return apiCallInterface.setDevices(mid, device_id, email, type, unique_id)
    }

    fun setOrder(mid: String, checkout_token: String?): Single<JsonElement> {
        return apiCallInterface.setOrder(mid, checkout_token)
    }

    fun getList(list: List<Storefront.ProductVariantEdge>): Observable<Storefront.ProductVariantEdge> {
        return Observable.fromIterable(list)
    }

    fun getProductList(list: List<Storefront.ProductEdge>): Observable<Storefront.ProductEdge> {
        return Observable.fromIterable(list)
    }

    fun getProductListSlider(list: List<Storefront.Product>): Observable<Storefront.Product> {
        return Observable.fromIterable(list)
    }

    fun getArModels(list: MutableList<MediaModel>): Observable<MediaModel> {
        return Observable.fromIterable(list)
    }

    fun getJSonArray(list: JsonArray): Observable<JsonElement> {
        return Observable.fromIterable(list)
    }

    fun insertData(data: AppLocalData) {
        appdatabase.appLocalDataDaoDao().insert(data)
    }

    fun updateData(data: AppLocalData) {
        appdatabase.appLocalDataDaoDao().update(data)
    }

    fun deleteLocalData() {
        appdatabase.appLocalDataDaoDao().delete()
    }

    fun insertWishListData(data: ItemData) {
        appdatabase.itemDataDao.insert(data)
    }

    fun getSingleData(id: String): ItemData {
        return appdatabase.itemDataDao.getSingleData(id)
    }

    fun deleteSingleData(data: ItemData) {
        appdatabase.itemDataDao.delete(data)
    }

    fun getSingle(data: AppLocalData): Single<AppLocalData> {
        return Single.just(data)
    }

    fun getSingLeItem(id: String): CartItemData {
        return appdatabase.cartItemDataDao.getSingleData(id)
    }


    fun addSingLeItem(data: CartItemData) {
        appdatabase.cartItemDataDao.insert(data)
    }

    fun updateSingLeItem(data: CartItemData) {
        appdatabase.cartItemDataDao.update(data)
    }

    fun deleteSingLeItem(data: CartItemData) {
        appdatabase.cartItemDataDao.delete(data)
    }

    fun deletecart() {
        appdatabase.cartItemDataDao.deleteCart()
    }

    fun insertUserData(data: UserLocalData) {
        appdatabase.appLocalDataDaoDao().insertUserData(data)
    }

    fun updateUserData(data: UserLocalData) {
        appdatabase.appLocalDataDaoDao().updateUserData(data)
    }

    fun saveaccesstoken(token: CustomerTokenData) {
        appdatabase.appLocalDataDaoDao().InsertCustomerToken(token)

    }

    fun updateAccessToken(data: CustomerTokenData) {
        appdatabase.appLocalDataDaoDao().UpdateCustomerToken(data)
    }

    fun deleteWishListData() {
        appdatabase.itemDataDao.deleteall()
    }

    fun deleteUserData() {
        appdatabase.appLocalDataDaoDao().deletealldata()
        appdatabase.appLocalDataDaoDao().deleteall()
    }

    fun insertPreviewData(data: LivePreviewData) {
        appdatabase.getLivePreviewDao().insert(data)
    }

    fun updatePreviewData(data: LivePreviewData) {
        appdatabase.getLivePreviewDao().update(data)
    }

    fun getPreviewData(): List<LivePreviewData> {
        return appdatabase.getLivePreviewDao().getPreviewDetails
    }

    fun getProductReviews(mid: String, product_id: String, page: Int): Single<JsonElement> {
        return apiCallInterface.getReviewsList(mid, product_id, page)
    }

    fun getbadgeReviews(mid: String, product_id: String): Single<JsonElement> {
        return apiCallInterface.getBadges(mid, product_id)
    }

    fun getcreateReview(
        mid: String,
        reviewRating: String,
        product_id: String,
        reviewAuthor: String,
        reviewEmail: String,
        reviewTitle: String,
        reviewBody: String
    ): Single<JsonElement> {
        return apiCallInterface.createReview(
            mid,
            reviewRating,
            product_id,
            reviewAuthor,
            reviewEmail,
            reviewTitle,
            reviewBody
        )
    }

    fun sizeChart(
        shop: String,
        source: String,
        product_id: String,
        tags: String,
        vendor: String
    ): String {
        return apiCallInterface.getSizeChart(shop, source, product_id, tags, vendor)
    }

    fun judgemeReviewCount(
        product_id: String,
        apiToken: String,
        shopDomain: String
    ): Single<JsonElement> {
        return apiCallInterface.getJudgemeReviewCount(apiToken, shopDomain, product_id)
    }

    fun judgemeReviewIndex(
        apiToken: String,
        shopDomain: String,
        per_page: Int,
        page: Int,
        product_id: String
    ): Single<JsonElement> {
        return apiCallInterface.getJudgemeIndex(apiToken, shopDomain, per_page, page, product_id)
    }

    fun judgemeReviewCreate(params: JsonObject): Single<JsonElement> {
        return apiCallInterface.createJudgemeReview(params)
    }

    fun judgemeProductID(
        url: String,
        handle: String,
        apiToken: String,
        shopDomain: String
    ): Single<JsonElement> {
        return apiCallInterface.getJudgemeProductID(url, apiToken, shopDomain, handle)
    }

    fun AliReviewInstallStatus(): Single<JsonElement> {
        return apiCallInterface.getAlireviewStatus()
    }

    fun getAliProductReview(
        shop_id: String,
        product_id: String,
        currentPage: Int
    ): Single<JsonElement> {
        return apiCallInterface.getAliProductReview(shop_id, product_id, currentPage)
    }

    fun getRewards(x_guid: String, x_api_key: String, customer_email: String, customer_id: String): Single<JsonElement> {
        return apiCallInterface.getrewards(x_guid, x_api_key/*, customer_email, customer_id*/)
    }

    fun redeemPoints(x_guid: String, x_api_key: String, customer_external_id: String, customer_email: String, redemption_option_id: String): Single<JsonElement> {
        return apiCallInterface.redeemPoints(x_guid, x_api_key, customer_external_id, customer_email, redemption_option_id)
    }

    fun earnRewards(x_guid: String, x_api_key: String): Single<JsonElement> {
        return apiCallInterface.earnRewards(x_guid, x_api_key)
    }

    fun myrewards(x_guid: String, x_api_key: String, customer_email: String, customer_id: String): Single<JsonElement> {
        return apiCallInterface.myrewards(x_guid, x_api_key, customer_email, customer_id, true, true)
    }

    fun referfriend(x_guid: String, x_api_key: String, customer_id: String, emails: String): Single<JsonElement> {
        return apiCallInterface.referfriend(x_guid, x_api_key, customer_id, emails)
    }
    fun validateDelivery(jsonObject: HashMap<String, String>): Single<JsonObject> {
        return apiCallInterface.validateDelivery(jsonObject)
    }

    fun localDelivery(jsonObject: HashMap<String, String>): Single<JsonObject> {
        return apiCallInterface.localDelivery(jsonObject)
    }

    fun yotpoauthentiate(client_id: String, client_secret: String, grant_type: String): Single<JsonElement> {
        return apiCallInterface.yotpoauthentiate(client_id,client_secret,grant_type)
    }

    fun yotpocretereview(appkey: String, sku: String, product_title: String, product_url: String, display_name: String, email: String, review_content: String, review_title: String, review_score: String): Single<JsonElement> {
        return apiCallInterface.yotpocretereview(appkey,sku,product_title,product_url,display_name,email,review_content,review_title,review_score)
    }

    fun discountcodeapply(mid: String, customer_code: String): Single<JsonElement> {
        return apiCallInterface.discountcodeapply(mid, customer_code)
    }


    fun localDeliveryy(jsonObject: HashMap<String, String>): Single<JsonObject> {
        return apiCallInterface.localDeliveryy(jsonObject)
    }
    fun DeliveryStatus(mid: String): Single<JsonObject> {
        return apiCallInterface.DeliveryStatus(mid)
    }

    fun storeDelivery(jsonObject: HashMap<String, String>): Single<JsonObject> {
        return apiCallInterface.localDelivery(jsonObject)
    }
    fun menuCollection(mid: String, tags: String): Single<JsonElement> {
        return apiCallInterface.getMenuCollection(mid, tags)

    }
    fun getCcollectionProductsbyTags(mid: String, handle: String,sort:String,page:String,tags:String): Single<JsonElement> {
        return apiCallInterface.getCollectionProductsbyTags(mid, handle,sort,page,tags)

    }
    fun getUserLogin(mid: String, email: String): Single<JsonElement> {
        return apiCallInterface.getuserLogin(mid, email)
    }
}
