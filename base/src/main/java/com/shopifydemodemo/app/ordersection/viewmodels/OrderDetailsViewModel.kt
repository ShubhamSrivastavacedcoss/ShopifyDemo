package com.shopifydemodemo.app.ordersection.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.network_transaction.CustomResponse
import com.shopifydemodemo.app.network_transaction.doGraphQLQueryGraph
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.shopifyqueries.Query
import com.shopifydemodemo.app.utils.GraphQLResponse
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class OrderDetailsViewModel(var repository: Repository) : ViewModel() {
    var presentmentCurrency: String? = null
    private val disposables = CompositeDisposable()
    val recommendedLiveData = MutableLiveData<GraphQLResponse>()
    lateinit var context: Context

    fun setPresentmentCurrencyForModel(): Boolean {
        val isadded = booleanArrayOf(false)
        try {
            val executor = Executors.newSingleThreadExecutor()
            val callable = Callable {
                if (repository.localData[0].currencycode == null) {
                    presentmentCurrency = "nopresentmentcurrency"
                } else {
                    presentmentCurrency = repository.localData[0].currencycode
                }
                isadded[0] = true
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

    fun shopifyRecommended(productID: String) {
        var currency_list = ArrayList<Storefront.CurrencyCode>()
        if (presentmentCurrency != "nopresentmentcurrency") {
            currency_list.add(Storefront.CurrencyCode.valueOf(presentmentCurrency!!))
        }
        getRecommendedProducts(currency_list, productID)
    }

    private fun getRecommendedProducts(currencyList: ArrayList<Storefront.CurrencyCode>, productID: String) {
        try {
            doGraphQLQueryGraph(repository, Query.recommendedProducts(productID, currencyList), customResponse = object : CustomResponse {
                override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                    invokeRecommended(result)
                }
            }, context = context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun invokeRecommended(result: GraphCallResult<Storefront.QueryRoot>) {
        if (result is GraphCallResult.Success<*>) {
            recommendedLiveData.setValue(GraphQLResponse.success(result as GraphCallResult.Success<*>))
        } else {
            recommendedLiveData.setValue(GraphQLResponse.error(result as GraphCallResult.Failure))
        }
    }
}