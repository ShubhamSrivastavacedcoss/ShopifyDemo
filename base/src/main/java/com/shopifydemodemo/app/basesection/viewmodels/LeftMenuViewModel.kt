package com.shopifydemodemo.app.basesection.viewmodels

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.dbconnection.entities.ItemData
import com.shopifydemodemo.app.dbconnection.entities.LivePreviewData
import com.shopifydemodemo.app.network_transaction.CustomResponse
import com.shopifydemodemo.app.network_transaction.doGraphQLQueryGraph
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.sharedprefsection.MagePrefs
import com.shopifydemodemo.app.shopifyqueries.Query
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.GraphQLResponse
import com.shopifydemodemo.app.utils.Status
import com.shopifydemodemo.app.utils.Urls

import java.util.HashMap
import java.util.Objects

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class LeftMenuViewModel(var repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val responseLiveData = MutableLiveData<ApiResponse>()
    val message = MutableLiveData<String>()
    val data = MutableLiveData<HashMap<String, String>>()
    private val currencyResponseLiveData = MutableLiveData<List<Storefront.CurrencyCode>>()
    private val handler = Handler()
    var context: Context? = null
    val isLoggedIn: Boolean
        get() {
            var loggedin = runBlocking(Dispatchers.IO) {
                return@runBlocking repository.isLogin
            }
            return loggedin
        }

    fun Response(): MutableLiveData<ApiResponse> {
        getMenus()
        return responseLiveData
    }

    var cartCount: Int = 0
        get() {
            val count = intArrayOf(0)
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    if (repository.allCartItems.size > 0) {
                        count[0] = repository.allCartItems.size
                    }
                    count[0]
                }
                val future = executor.submit(callable)
                count[0] = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return count[0]
        }
    var wishListcount: Int = 0
        get() {
            val count = intArrayOf(0)
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    if (repository.wishListData.size > 0) {
                        count[0] = repository.wishListData.size
                    }
                    count[0]
                }
                val future = executor.submit(callable)
                count[0] = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return count[0]
        }

    fun fetchUserData() {
        try {
            val runnable = Runnable {
                val hashdata = HashMap<String, String>()
                if (repository.isLogin) {
                    val localData = repository.allUserData[0]
                    hashdata.put("firstname", localData.firstname!!)
                    hashdata.put("secondname", localData.lastname!!)
                    hashdata.put("tag", "login")
                    Log.i("MageNative", "LeftMenuResume 2" + localData.firstname!!)

                } else {
                    Log.i("MageNative", "LeftMenuResume 2" + "Sign")
                    hashdata["firstname"] = context?.getString(R.string.sign_first)!!
                    hashdata["secondname"] = context?.getString(R.string.in_last)!!
                    hashdata["tag"] = "Sign In"
                }
                handler.post { data.value = hashdata }
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getMenus() {
        try {
            disposables.add(repository.getMenus(Urls(MyApplication.context)!!.mid, MagePrefs.getLanguage()!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                    { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
                ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun currencyResponse(): MutableLiveData<List<Storefront.CurrencyCode>> {
        getCurrency()
        return currencyResponseLiveData
    }

    private fun getCurrency() {
        try {
            doGraphQLQueryGraph(
                repository,
                Query.shopDetails,
                customResponse = object : CustomResponse {
                    override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                        invoke(result)
                    }
                },
                context = context!!
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCleared() {
        disposables.clear()
    }

    private operator fun invoke(result: GraphCallResult<Storefront.QueryRoot>): Unit {
        if (result is GraphCallResult.Success<*>) {
            consumeResponse(GraphQLResponse.success(result as GraphCallResult.Success<*>))
        } else {
            consumeResponse(GraphQLResponse.error(result as GraphCallResult.Failure))
        }
        return Unit
    }

    private fun consumeResponse(reponse: GraphQLResponse) {
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
                    currencyResponseLiveData.setValue(
                        Objects.requireNonNull<Storefront.QueryRoot>(
                            result.data
                        ).getShop().getPaymentSettings().getEnabledPresentmentCurrencies()
                    )
                }
            }
            Status.ERROR -> message.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun setCurrencyData(currencyCode: String?) {
        val runnable = Runnable {
            val appLocalData = repository.localData[0]
            appLocalData.currencycode = currencyCode
            repository.updateData(appLocalData)
        }
        Thread(runnable).start()

    }

    fun logOut() {
        val runnable = Runnable {
            Log.i("MageNative", "LeftMenuResume 5")
            repository.deletecart()
            repository.deleteWishListData()
            repository.deleteUserData()
            fetchUserData()
        }
        Thread(runnable).start()
    }

    fun deletLocal() {
        GlobalScope.launch(Dispatchers.IO) {
            repository.deleteLocalData()
        }
    }

    fun deleteData(product_id: String) {
        try {
            val runnable = Runnable {
                try {
                    val data = repository.getSingleData(product_id)
                    repository.deleteSingleData(data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    public fun isInwishList(product_id: String): Boolean {
        val isadded = booleanArrayOf(false)
        try {
            val executor = Executors.newSingleThreadExecutor()
            val callable = Callable {
                if (repository.getSingleData(product_id) != null) {

                    Log.i("MageNative", "item already in wishlist : ")
                    isadded[0] = true
                }
                isadded[0]
            }
            val future = executor.submit(callable)
            isadded[0] = future.get()
            executor.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isadded[0]
    }

    fun setWishList(product_id: String): Boolean {
        val isadded = booleanArrayOf(false)
        try {
            val executor = Executors.newSingleThreadExecutor()
            val callable = Callable {
                if (repository.getSingleData(product_id) == null) {
                    Log.i("MageNative", "WishListCount : " + repository.wishListData.size)
                    val data = ItemData()
                    data.product_id = product_id
                    repository.insertWishListData(data)
                    Log.i("MageNative", "WishListCount 2: " + repository.wishListData.size)
                    isadded[0] = true
                }
                isadded[0]
            }
            val future = executor.submit(callable)
            isadded[0] = future.get()
            executor.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isadded[0]
    }

    fun insertPreviewData(data: JSONObject) {
        val runnable = Runnable {
            var lpreview = repository.getPreviewData()
            if (lpreview.size == 0) {
                var preview = LivePreviewData(
                    data.getString("mid"),
                    data.getString("shopUrl"),
                    data.getString("token")
                )
                repository.insertPreviewData(preview)
            } else {
                var preview = lpreview.get(0)
                preview.mid = data.getString("mid")
                preview.shopurl = data.getString("shopUrl")
                preview.apikey = data.getString("token")
                repository.updatePreviewData(preview)
            }
        }
        Thread(runnable).start()
    }
}
