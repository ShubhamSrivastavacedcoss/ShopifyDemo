package com.shopifydemodemo.app.addresssection.viewmodels

import android.content.Context

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopify.graphql.support.ID
import com.shopifydemodemo.app.addresssection.models.Address
import com.shopifydemodemo.app.dbconnection.entities.CustomerTokenData
import com.shopifydemodemo.app.network_transaction.CustomResponse
import com.shopifydemodemo.app.network_transaction.doGraphQLMutateGraph
import com.shopifydemodemo.app.network_transaction.doGraphQLQueryGraph
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.shopifyqueries.MutationQuery
import com.shopifydemodemo.app.shopifyqueries.Query
import com.shopifydemodemo.app.utils.GraphQLResponse
import com.shopifydemodemo.app.utils.Status

class AddressModel(private val repository: Repository) : ViewModel() {
    private var addr: Address? = null
    private var msg: String? = null
    private var data: CustomerTokenData? = null
    var addresscursor: String = "nocursor"
        set(addresscursor) {
            field = addresscursor
            getAddressList()
        }
    lateinit var context: Context
    val message = MutableLiveData<String>()
    val sheet = MutableLiveData<Boolean>()
    val editaddress = MutableLiveData<Address>()
    private val address = MutableLiveData<MutableList<Storefront.MailingAddressEdge>>()
    val addresses: MutableLiveData<MutableList<Storefront.MailingAddressEdge>>
        get() {
            getAddressList()
            return address
        }

    private fun getAddressList() {
        try {
            data = repository.accessToken[0]
            doGraphQLQueryGraph(repository, Query.getAddressList(data!!.customerAccessToken, addresscursor), customResponse = object : CustomResponse {
                override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                    invoke(result)
                }
            }, context = context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                    address.setValue(result.data!!.customer.addresses.edges)
                }
            }
            Status.ERROR -> message.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun setSheet() {
        sheet.value = true
    }

    fun deleteAddress(msg: String, adress: Address) {
        try {
            this.msg = msg
            doGraphQLMutateGraph(repository, (MutationQuery.deleteCustomerAddress(data!!.customerAccessToken, adress.address_id)), customResponse = object : CustomResponse {
                override fun onSuccessMutate(result: GraphCallResult<Storefront.Mutation>) {
                    deleteAddressinvoke(result)
                }
            }, context = context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun deleteAddressinvoke(graphCallResult: GraphCallResult<Storefront.Mutation>): Unit {
        try {
            if (graphCallResult is GraphCallResult.Success<*>) {
                consumeAddressResponse(GraphQLResponse.success(graphCallResult as GraphCallResult.Success<*>))
            } else {
                consumeAddressResponse(GraphQLResponse.error(graphCallResult as GraphCallResult.Failure))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Unit
    }

    private fun consumeAddressResponse(reponse: GraphQLResponse) {
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
                    message.setValue(errormessage.toString())
                } else {
                    val errors = result.data!!.customerAddressDelete.customerUserErrors
                    if (errors.size > 0) {
                        val iterator = errors.iterator()
                        var err = ""
                        while (iterator.hasNext()) {
                            val error = iterator.next() as Storefront.CustomerUserError
                            err += error.message
                        }
                        message.setValue(err)
                    } else {
                        message.setValue(msg)
                        repository.graphClient.clearCache()
                    }
                }
            }
            Status.ERROR -> message.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun addAddress(input: Storefront.MailingAddressInput) {
        try {
            doGraphQLMutateGraph(repository, MutationQuery.addAddress(input, data!!.customerAccessToken), customResponse = object : CustomResponse {
                override fun onSuccessMutate(result: GraphCallResult<Storefront.Mutation>) {
                    addAddressinvoke(result)
                }
            }, context = context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addAddressinvoke(graphCallResult: GraphCallResult<Storefront.Mutation>): Unit {
        try {
            if (graphCallResult is GraphCallResult.Success<*>) {
                consumeAddAddressResponse(GraphQLResponse.success(graphCallResult as GraphCallResult.Success<*>))
            } else {
                consumeAddAddressResponse(GraphQLResponse.error(graphCallResult as GraphCallResult.Failure))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Unit
    }

    private fun consumeAddAddressResponse(reponse: GraphQLResponse) {
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
                    message.setValue(errormessage.toString())
                } else {
                    val errors = result.data!!.customerAddressCreate.customerUserErrors
                    if (errors.size > 0) {
                        val iterator = errors.iterator()
                        var err = ""
                        while (iterator.hasNext()) {
                            val error = iterator.next() as Storefront.CustomerUserError
                            err += error.message
                        }
                        message.setValue(err)
                    } else {
                        repository.graphClient.clearCache()
                        addresscursor = "nocursor"
                    }
                }
            }
            Status.ERROR -> message.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun setAddress(address: Address) {
        addr = address
        editaddress.value = address
    }

    fun updateAddress(input: Storefront.MailingAddressInput, address_id: ID?) {
        try {
            doGraphQLMutateGraph(repository,MutationQuery.updateAddress(input, data!!.customerAccessToken, address_id),customResponse = object :CustomResponse{
                override fun onSuccessMutate(result: GraphCallResult<Storefront.Mutation>) {
                    updateAddressinvoke(result)
                }
            },context = context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun updateAddressinvoke(graphCallResult: GraphCallResult<Storefront.Mutation>): Unit {
        try {
            if (graphCallResult is GraphCallResult.Success<*>) {
                consumeUpdateAddressResponse(GraphQLResponse.success(graphCallResult as GraphCallResult.Success<*>))
            } else {
                consumeUpdateAddressResponse(GraphQLResponse.error(graphCallResult as GraphCallResult.Failure))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Unit
    }

    private fun consumeUpdateAddressResponse(reponse: GraphQLResponse) {
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
                    message.setValue(errormessage.toString())
                } else {
                    val errors = result.data!!.customerAddressUpdate.customerUserErrors
                    if (errors.size > 0) {
                        val iterator = errors.iterator()
                        var err = ""
                        while (iterator.hasNext()) {
                            val error = iterator.next() as Storefront.CustomerUserError
                            err += error.message
                        }
                        message.setValue(err)
                    } else {
                        repository.graphClient.clearCache()
                        val address = result.data!!.customerAddressUpdate.customerAddress
                        addr!!.firstName = address.firstName
                        addr!!.lastName = address.lastName
                        addr!!.address1 = address.address1
                        addr!!.address2 = address.address2
                        addr!!.city = address.city
                        addr!!.province = address.province
                        addr!!.country = address.country
                        addr!!.zip = address.zip
                        addr!!.phone = address.phone
                        addr!!.address_id = address.id
                    }
                }
            }
            Status.ERROR -> message.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }
}
