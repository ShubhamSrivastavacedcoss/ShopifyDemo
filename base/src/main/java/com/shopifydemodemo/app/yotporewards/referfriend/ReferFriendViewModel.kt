package com.shopifydemodemo.app.yotporewards.referfriend

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopifydemodemo.app.network_transaction.CustomResponse
import com.shopifydemodemo.app.network_transaction.doRetrofitCall
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.sharedprefsection.MagePrefs
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.Urls
import com.google.gson.JsonElement
import io.reactivex.disposables.CompositeDisposable

class ReferFriendViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    lateinit var context: Context
    var referfriend = MutableLiveData<ApiResponse>()


    fun sendReferral(emails: String) {
        doRetrofitCall(repository.referfriend(Urls.XGUID, Urls.X_API_KEY, MagePrefs.getCustomerID()
                ?: "", emails), disposables, object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                referfriend.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                referfriend.value = ApiResponse.error(error)
            }
        }, context = context)
    }

}