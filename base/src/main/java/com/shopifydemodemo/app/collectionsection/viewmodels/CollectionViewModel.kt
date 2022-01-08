package com.shopifydemodemo.app.collectionsection.viewmodels

import android.content.Context

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopifydemodemo.app.network_transaction.CustomResponse
import com.shopifydemodemo.app.network_transaction.doGraphQLQueryGraph
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.shopifyqueries.Query
import com.shopifydemodemo.app.utils.GraphQLResponse
import com.shopifydemodemo.app.utils.Status

class CollectionViewModel(private val repository: Repository) : ViewModel() {
    var cursor = "nocursor"
    private val responsedata = MutableLiveData<List<Storefront.CollectionEdge>>()
    val message = MutableLiveData<String>()
    lateinit var context: Context
    fun Response(): MutableLiveData<List<Storefront.CollectionEdge>> {
        getCollectionData()
        return responsedata
    }

    private fun getCollectionData() {
        try {
            doGraphQLQueryGraph(repository, Query.getCollections(cursor), customResponse = object : CustomResponse {
                override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                    invoke(result)
                }
            }, context = context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private operator fun invoke(result: GraphCallResult<Storefront.QueryRoot>) {
        if (result is GraphCallResult.Success<*>) {
            consumeResponse(GraphQLResponse.success(result as GraphCallResult.Success<*>))
        } else {
            consumeResponse(GraphQLResponse.error(result as GraphCallResult.Failure))
        }
    }

    private fun consumeResponse(reponse: GraphQLResponse) {
        when (reponse.status) {
            Status.SUCCESS -> {
                val result = (reponse.data as GraphCallResult.Success<Storefront.QueryRoot>).response
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
                    responsedata.setValue(result.data!!.collections.edges)
                }
            }
            Status.ERROR -> message.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }
}
