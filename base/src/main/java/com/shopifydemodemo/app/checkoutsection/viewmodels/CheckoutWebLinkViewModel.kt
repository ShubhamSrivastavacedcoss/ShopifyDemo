package com.shopifydemodemo.app.checkoutsection.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopifydemodemo.app.dbconnection.entities.CustomerTokenData
import com.shopifydemodemo.app.dbconnection.entities.UserLocalData
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.utils.ApiResponse
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CheckoutWebLinkViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val responseLiveData = MutableLiveData<ApiResponse>()
    lateinit var context: Context
    var customeraccessToken: CustomerTokenData
        get() {
            val customerToken = runBlocking(Dispatchers.IO) {
                return@runBlocking repository.accessToken[0]
            }
            return customerToken
        }
        set(value) {}
    val isLoggedIn: Boolean
        get() {
            val loggedin = runBlocking(Dispatchers.IO) {
                return@runBlocking repository.isLogin
            }
            return loggedin
        }
    val data: UserLocalData?
        get() {
            val user = arrayOf<UserLocalData>()
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    user[0] = repository.allUserData[0]
                    user[0]
                }
                val future = executor.submit(callable)
                user[0] = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return user[0]
        }

    fun setOrder(mid: String, checkout_token: String?) {
        try {
            val postData = repository.setOrder(mid, checkout_token)
//            doRetrofitCall(postData, disposables, customResponse = object : CustomResponse {
//                override fun onSuccessRetrofit(result: JsonElement) {
//                    responseLiveData.setValue(ApiResponse.success(result))
//                }
//
//                override fun onErrorRetrofit(error: Throwable) {
//                    responseLiveData.setValue(ApiResponse.error(error))
//                }
//            }, context = context)

            disposables.add(postData
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                responseLiveData.value = ApiResponse.success(result)
                            },
                            { throwable ->
                                responseLiveData.value = ApiResponse.error(throwable)
                            }
                    ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteCart() {
        try {
            val runnable = Runnable { repository.deletecart() }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCleared() {
        disposables.clear()
    }
}
