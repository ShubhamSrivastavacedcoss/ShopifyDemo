package com.shopifydemodemo.app.network_transaction

import com.google.gson.JsonElement
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront

interface CustomResponse {
    fun onSuccessMutate(result: GraphCallResult<Storefront.Mutation>) {}
    fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {}
    fun onSuccessRetrofit(result: JsonElement) {}
    fun onErrorRetrofit(error: Throwable) {}
}