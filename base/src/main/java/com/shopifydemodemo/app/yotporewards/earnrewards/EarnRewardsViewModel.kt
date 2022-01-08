package com.shopifydemodemo.app.yotporewards.earnrewards

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopifydemodemo.app.network_transaction.CustomResponse
import com.shopifydemodemo.app.network_transaction.doRetrofitCall
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.Urls
import com.google.gson.JsonElement
import io.reactivex.disposables.CompositeDisposable

class EarnRewardsViewModel(private val repository: Repository) : ViewModel() {
    private val disposables = CompositeDisposable()
    lateinit var context: Context
    var earnrewards = MutableLiveData<ApiResponse>()

    fun earnRewards() {
        doRetrofitCall(repository.earnRewards(Urls.XGUID, Urls.X_API_KEY), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                earnrewards.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                earnrewards.value = ApiResponse.error(error)
            }
        }, context = context)
    }

}