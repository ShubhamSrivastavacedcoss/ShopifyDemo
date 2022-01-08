package com.shopifydemodemo.app.basesection.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.basesection.models.FeaturesModel
import com.shopifydemodemo.app.dbconnection.entities.AppLocalData
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.sharedprefsection.MagePrefs
import com.shopifydemodemo.app.shopifyqueries.MutationQuery
import com.shopifydemodemo.app.shopifyqueries.Query
import com.shopifydemodemo.app.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.Runnable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


class SplashViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val responseLiveData = MutableLiveData<LocalDbResponse>()
    private val apiresponseLiveData = MutableLiveData<ApiResponse>()
    private val fireBaseResponseMutableLiveData = MutableLiveData<FireBaseResponse>()
    private val notification_compaign = MutableLiveData<Boolean>()
    val errorMessageResponse = MutableLiveData<String>()
    var filteredproducts: MutableLiveData<MutableList<Storefront.ProductEdge>>? =
        MutableLiveData<MutableList<Storefront.ProductEdge>>()
    var presentmentcurrency: String? = null
    var appLocalData: AppLocalData = AppLocalData()
    val message = MutableLiveData<String>()
    var searchcursor: String = "nocursor"
    var firebaseAnalytics: FirebaseAnalytics

    init {
        firebaseAnalytics = Firebase.analytics
    }

    companion object {
        var featuresModel: FeaturesModel = FeaturesModel()
    }

    private val TAG = "SplashViewModel"

    val isLogin: Boolean
        get() {
            var loggedin = runBlocking(Dispatchers.IO) {
                return@runBlocking repository.isLogin
            }
            return loggedin
        }

    fun Response(shop: String): MutableLiveData<LocalDbResponse> {
        connectFirebaseForTrial(shop)
        return responseLiveData
    }

    fun setPresentmentCurrencyForModel() {
        try {
            val runnable = Runnable {
                if (!repository.localData.isEmpty()) {
                    if (repository.localData[0].currencycode == null) {
                        presentmentcurrency = "nopresentmentcurrency"
                    } else {
                        presentmentcurrency = repository.localData[0].currencycode
                    }
                }
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getNotificationCompaign(): MutableLiveData<Boolean> {
        return notification_compaign
    }

    fun firebaseResponse(): MutableLiveData<FireBaseResponse> {
        connectFireBaseForSplashData()
        return fireBaseResponseMutableLiveData
    }

    private fun getCurrency() {
        try {
            var call = repository.graphClient.queryGraph(Query.shopDetails)
            call.enqueue { result: GraphCallResult<Storefront.QueryRoot> -> currencyResponse(result) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getProductsByKeywords(keyword: String): Unit {
        var currency_list = ArrayList<Storefront.CurrencyCode>()
        if (presentmentcurrency != "nopresentmentcurrency") {
            currency_list.add(Storefront.CurrencyCode.valueOf(presentmentcurrency!!))
        }
        try {
            val call = repository.graphClient.queryGraph(
                Query.getSearchProducts(
                    keyword,
                    searchcursor,
                    currency_list
                )
            )
            call.enqueue(Handler(Looper.getMainLooper())) { result: GraphCallResult<Storefront.QueryRoot> ->
                this.invokeProduct(
                    result
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Unit
    }

    private fun invokeProduct(result: GraphCallResult<Storefront.QueryRoot>) {
        if (result is GraphCallResult.Success<*>) {
            consumeProductResponse(GraphQLResponse.success(result as GraphCallResult.Success<*>))
        } else {
            consumeProductResponse(GraphQLResponse.error(result as GraphCallResult.Failure))
        }
    }

    private fun consumeProductResponse(reponse: GraphQLResponse) {
        when (reponse.status) {
            Status.SUCCESS -> {
                val result =
                    (reponse.data as GraphCallResult.Success<Storefront.QueryRoot>).response
                if (result.hasErrors) {
                    val errors = result.errors
                    val iterator = errors.iterator()
                    val errormessage = StringBuilder()
                    var error: Error? = null
                    while (iterator.hasNext()) {
                        error = iterator.next()
                        errormessage.append(error.message())
                    }
                    Log.i("MageNative", "1" + errormessage);
                    message.setValue(errormessage.toString())
                } else {
                    filterProduct(result.data!!.products.edges)
                }
            }
            Status.ERROR -> {
                Log.i("MageNative", "2" + reponse.error!!.error.message);
                message.setValue(reponse.error!!.error.message)
            }
            else -> {
            }
        }
    }

    fun filterProduct(list: MutableList<Storefront.ProductEdge>) {
        try {
            disposables.add(repository.getProductList(list)
                .subscribeOn(Schedulers.io())
//                     { x -> x.node.availableForSale }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> filteredproducts!!.value = result })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun currencyResponse(result: GraphCallResult<Storefront.QueryRoot>) {
        if (result is GraphCallResult.Success<*>) {
            consumeResponseCurrency(GraphQLResponse.success(result as GraphCallResult.Success<*>))
        } else {
            consumeResponseCurrency(GraphQLResponse.error(result as GraphCallResult.Failure))
        }
    }


    private fun consumeResponseCurrency(reponse: GraphQLResponse) {
        when (reponse.status) {
            Status.SUCCESS -> {
                val result =
                    (reponse.data as GraphCallResult.Success<Storefront.QueryRoot>).response
                if (result.hasErrors) {
                    val errors = result.errors
                    val iterator = errors.iterator()
                    val errormessage = StringBuilder()
                    var error: Error? = null
                    while (iterator.hasNext()) {
                        error = iterator.next()
                        errormessage.append(error.message())
                    }
                    message.setValue(errormessage.toString())
                } else {

                    if (repository.localData.size == 0) {
                        appLocalData.currencycode =
                            result.data?.getShop()?.paymentSettings?.currencyCode.toString()
                        MagePrefs.setCurrency(appLocalData.currencycode ?: "")
                        repository.insertData(appLocalData)
                    }
                }
            }
            Status.ERROR -> message.postValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    private fun connectFirebaseForTrial(shop: String) {
        try {
            MyApplication.dataBaseReference?.child("additional_info")?.child("validity")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val value = dataSnapshot.getValue(Boolean::class.java)!!
                        val runnable = Runnable {
                            Log.i("MageNative:", "TrialExpired$value")
                            Log.i("MageNative:", "LocalData" + repository.localData)

                            if (repository.localData.size == 0) {
                                appLocalData?.isIstrialexpire = value
                                getCurrency()
                            } else {
                                appLocalData = repository.localData[0]
                                appLocalData!!.isIstrialexpire = value
                                MagePrefs.setCurrency(appLocalData.currencycode ?: "")
                                repository.updateData(appLocalData)
                            }
                            Log.i(
                                "MageNative:", "Currency" +
                                        appLocalData.currencycode
                            )
                            disposables.add(repository.getSingle(appLocalData)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    { result ->
                                        responseLiveData.setValue(
                                            LocalDbResponse.success(
                                                result
                                            )
                                        )
                                    },
                                    { throwable ->
                                        responseLiveData.setValue(
                                            LocalDbResponse.error(
                                                throwable
                                            )
                                        )
                                    }
                                ))
                        }
                        Thread(runnable).start()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.i("DBConnectionError", "" + databaseError.details)
                        Log.i("DBConnectionError", "" + databaseError.message)
                        Log.i("DBConnectionError", "" + databaseError.code)
                    }
                })
//            MyApplication.dataBaseReference?.child("additional_info")?.child("personalise")
//                ?.addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        Constant.ispersonalisedEnable = dataSnapshot.getValue(Boolean::class.java)!!
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        Log.i("DBConnectionError", "" + databaseError.details)
//                        Log.i("DBConnectionError", "" + databaseError.message)
//                        Log.i("DBConnectionError", "" + databaseError.code)
//                    }
//                })
            MyApplication.dataBaseReference?.child("additional_info")?.child("locale")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        /*if you are using multi language then comment this line*/
                        //   MagePrefs.setLanguage(dataSnapshot.getValue(String::class.java)!!)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.i("DBConnectionError", "" + databaseError.details)
                        Log.i("DBConnectionError", "" + databaseError.message)
                        Log.i("DBConnectionError", "" + databaseError.code)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun connectFireBaseForSplashData() {
        try {
            MyApplication.dataBaseReference?.child("additional_info")?.child("splash")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        fireBaseResponseMutableLiveData.setValue(
                            FireBaseResponse.success(
                                dataSnapshot
                            )
                        )
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.i("DBConnectionError", "" + databaseError.details)
                        Log.i("DBConnectionError", "" + databaseError.message)
                        Log.i("DBConnectionError", "" + databaseError.code)
                    }
                })

//            MyApplication.dataBaseReference?.child("additional_info")?.child("force_update")
//                ?.addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        try {
//                            Log.d(TAG, "onDataChange: " + dataSnapshot)
//                            val fource_update = dataSnapshot.value as Boolean
//                            featuresModel.forceUpdate = fource_update
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        Log.i("DBConnectionError", "" + databaseError.details)
//                        Log.i("DBConnectionError", "" + databaseError.message)
//                        Log.i("DBConnectionError", "" + databaseError.code)
//                    }
//                })

            MyApplication.dataBaseReference?.child("additional_info")?.child("maintenance_mode")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            Log.d(TAG, "onDataChange: " + dataSnapshot)
                            val maintenance = dataSnapshot.value as Boolean
                            MagePrefs.setMaintenanceMode(maintenance)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.i("DBConnectionError", "" + databaseError.details)
                        Log.i("DBConnectionError", "" + databaseError.message)
                        Log.i("DBConnectionError", "" + databaseError.code)
                    }
                })

            MyApplication.dataBaseReference?.child("features")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.d(TAG, "onDataChange: " + dataSnapshot.exists())
                        if (dataSnapshot.value != null) {
                            val featuresList = dataSnapshot.value as ArrayList<String>
                            for (i in 0..featuresList.size - 1) {
                                if (featuresList[i].equals("in-app-whislist", true)) {
                                    featuresModel.in_app_wishlist = true //Implemented
                                } else if (featuresList[i].equals("product-share", true)) {
                                    featuresModel.product_share = true //Implemented
                                } else if (featuresList[i].equals("multi-currency", true)) {
                                    featuresModel.multi_currency = true //Implemented
                                } else if (featuresList[i].equals("multi-language", true)) {
                                    featuresModel.multi_language = true
                                } else if (featuresList[i].equals(
                                        "abandoned-cart-campaigns",
                                        true
                                    )
                                ) {
                                    featuresModel.abandoned_cart_compaigns = true //Implemented
                                    notification_compaign.value = true //Implemented
                                } else if (featuresList[i].equals("augmented-reality", true)) {
                                    featuresModel.ardumented_reality = true //Implemented
                                } else if (featuresList[i].equals("qr-code-search-scanner", true)) {
                                    featuresModel.qr_code_search_scanner = true //Implemented
                                } else if (featuresList[i].equals("native_order_view", true)) {
                                    featuresModel.nativeOrderView = true
                                } else if (featuresList[i].equals("out_of_stock", true)) {
                                    featuresModel.outOfStock = true
                                } else if (featuresList[i].equals("reorder", true)) {
                                    featuresModel.reOrderEnabled = true
                                } else if (featuresList[i].equals("recommended_products", true)) {
                                    featuresModel.recommendedProducts = true
                                } else if (featuresList[i].equals("show_bottom_navigation", true)) {
                                    featuresModel.showBottomNavigation = true
                                } else if (featuresList[i].equals("add_to_cart", true)) {
                                    featuresModel.addCartEnabled = true
                                }
                            }
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.i("DBConnectionError", "" + databaseError.details)
                        Log.i("DBConnectionError", "" + databaseError.message)
                        Log.i("DBConnectionError", "" + databaseError.code)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCleared() {
        disposables.clear()
    }

    fun refreshTokenIfRequired() {
        val runnable = Runnable {
            if (repository.accessToken[0].expireTime != null) {
                Log.i("Magenative", "ExpireTime" + repository.accessToken[0].expireTime)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                var expiretime: Date? = null
                try {
                    expiretime = sdf.parse(
                        repository.accessToken[0].expireTime!!.split("t".toRegex())
                            .dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    )
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                val currentDate = Date()
                val diff = expiretime!!.time - currentDate.time
                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                Log.i("Magenative", "Days$days")
                if (days == 0L) {
                    renewToken(repository.accessToken[0].customerAccessToken)
                }
            }
        }
        Thread(runnable).start()
    }

    private fun renewToken(customerAccessToken: String?) {
        try {
            val call =
                repository.graphClient.mutateGraph(MutationQuery.renewToken(customerAccessToken))
            call.enqueue(Handler(Looper.getMainLooper())) { graphCallResult: GraphCallResult<Storefront.Mutation> ->
                this.invoke(
                    graphCallResult
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private operator fun invoke(graphCallResult: GraphCallResult<Storefront.Mutation>): Unit {
        if (graphCallResult is GraphCallResult.Success<*>) {
            consumeResponse(GraphQLResponse.success(graphCallResult as GraphCallResult.Success<*>))
        } else {
            consumeResponse(GraphQLResponse.error(graphCallResult as GraphCallResult.Failure))
        }
        return Unit
    }

    private fun consumeResponse(reponse: GraphQLResponse) {
        when (reponse.status) {
            Status.SUCCESS -> {
                val result = (reponse.data as GraphCallResult.Success<Storefront.Mutation>).response
                if (result.hasErrors) {
                    val errors = result.errors
                    val iterator = errors.iterator()
                    val errormessage = StringBuilder()
                    var error: Error? = null
                    while (iterator.hasNext()) {
                        error = iterator.next()
                        errormessage.append(error.message())
                    }
                    errorMessageResponse.setValue(errormessage.toString())
                    Log.i("MageNative", "" + errormessage)
                } else {
                    val errors = result.data!!.customerAccessTokenRenew.userErrors
                    if (errors.size > 0) {
                        val iterator = errors.iterator()
                        var err = ""
                        while (iterator.hasNext()) {
                            val error = iterator.next() as Storefront.UserError
                            err += error.message
                        }
                        errorMessageResponse.setValue(err)
                        Log.i("MageNative", "" + err)
                    } else {
                        val token = result.data!!.customerAccessTokenRenew.customerAccessToken
                        val data = repository.accessToken[0]
                        data.customerAccessToken = token.accessToken
                        data.expireTime = token.expiresAt.toString()
                        repository.updateAccessToken(data)
                    }
                }
            }
            Status.ERROR -> {
                errorMessageResponse.setValue(reponse.error!!.error.message)
                Log.i("MageNative", "" + reponse.error.error.message)
            }
            else -> {
            }
        }
    }

    fun sendTokenToServer(unique_id: String) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.i("MageNative", "token_error : " + task.exception!!)
                    return@OnCompleteListener
                }
                val token = task.result!!.token
                val params = Bundle()
                params.putString("device_token", token)
                firebaseAnalytics.logEvent("android_custom_log", params)

                Log.i("MageNative", "token$token")
                FirebaseMessaging.getInstance().subscribeToTopic("magenativeANDROID")
                disposables.add(repository.setDevice(
                    Urls(MyApplication.context)!!.mid,
                    token,
                    " ",
                    "android",
                    unique_id
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> apiresponseLiveData.setValue(ApiResponse.success(result)) },
                        { throwable -> apiresponseLiveData.setValue(ApiResponse.error(throwable)) }
                    ))
            })
    }
}



