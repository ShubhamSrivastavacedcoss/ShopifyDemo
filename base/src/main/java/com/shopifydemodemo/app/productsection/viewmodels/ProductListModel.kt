package com.shopifydemodemo.app.productsection.viewmodels

import android.content.Context
import android.util.Log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonElement

import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel.Companion.featuresModel
import com.shopifydemodemo.app.dbconnection.entities.ItemData
import com.shopifydemodemo.app.network_transaction.CustomResponse
import com.shopifydemodemo.app.network_transaction.doGraphQLQueryGraph
import com.shopifydemodemo.app.network_transaction.doRetrofitCall
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.shopifyqueries.Query
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.GraphQLResponse
import com.shopifydemodemo.app.utils.Status
import com.shopifydemodemo.app.utils.Urls

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class ProductListModel(var repository: Repository) : ViewModel() {
    private var categoryID = ""
    var shopID = ""
    var tags_ =""
    val presentmentCurrency: String
        get() {
            val currency = arrayOf("nopresentmentcurrency")
            try {
                val executor = Executors.newSingleThreadExecutor()
                val callable = Callable {
                    if (repository.localData[0].currencycode != null) {
                        currency[0] = repository.localData[0].currencycode!!
                    }
                    currency[0]
                }
                val future = executor.submit(callable)
                currency[0] = future.get()
                executor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return currency[0]
        }
    var collectionData: MutableLiveData<Storefront.Collection> =
        MutableLiveData<Storefront.Collection>()
    var categoryHandle = ""
    var cursor = "nocursor"
        set(cursor) {
            field = cursor
            Response()
        }
    var isDirection = false
    var sortKeys: Storefront.ProductCollectionSortKeys? = null
    var keys: Storefront.ProductSortKeys? = null
    var number = 10
    private val disposables = CompositeDisposable()
    val message = MutableLiveData<String>()
    val filteredproducts = MutableLiveData<MutableList<Storefront.ProductEdge>>()
    lateinit var context: Context
    var collectionTags: MutableLiveData<ApiResponse> = MutableLiveData<ApiResponse>()
    private val filerapiResponseData = MutableLiveData<ApiResponse>()
    fun getcategoryID(): String {
        return categoryID
    }

    val cartCount: Int
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

    fun setcategoryID(categoryID: String) {
        this.categoryID = categoryID
    }

    fun getcategoryHandle(): String {
        return categoryHandle
    }

    fun setcategoryHandle(categoryHandle: String) {
        this.categoryHandle = categoryHandle
    }

    fun Response() {
        if (!getcategoryID().isEmpty()) {
            getProductsById()
        }
        if (!getcategoryHandle().isEmpty()) {
            getProductsByHandle()
        }
        if (!shopID.isEmpty()) {
            getAllProducts()
        }
        getCollectionTags()
    }

    private fun getAllProducts() {
        var currency_list = ArrayList<Storefront.CurrencyCode>()
        if (presentmentCurrency != "nopresentmentcurrency") {
            currency_list.add(Storefront.CurrencyCode.valueOf(presentmentCurrency!!))
        }
        try {
            doGraphQLQueryGraph(
                repository,
                Query.getAllProducts(cursor, keys, isDirection, number, currency_list),
                customResponse = object : CustomResponse {
                    override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                        invoke(result)
                    }
                },
                context = context
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getProductsById() {
        var currency_list = ArrayList<Storefront.CurrencyCode>()
        if (presentmentCurrency != "nopresentmentcurrency") {
            currency_list.add(Storefront.CurrencyCode.valueOf(presentmentCurrency!!))
        }

        try {
            doGraphQLQueryGraph(
                repository,
                Query.getProductsById(
                    getcategoryID(),
                    cursor,
                    sortKeys,
                    isDirection,
                    number,
                    currency_list
                ),
                customResponse = object : CustomResponse {
                    override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                        invoke(result)
                    }
                },
                context = context
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getProductsByHandle() {
        var currency_list = ArrayList<Storefront.CurrencyCode>()
        if (presentmentCurrency != "nopresentmentcurrency") {
            currency_list.add(Storefront.CurrencyCode.valueOf(presentmentCurrency!!))
        }
        try {
            doGraphQLQueryGraph(
                repository,
                Query.getProductsByHandle(
                    getcategoryHandle(),
                    cursor,
                    sortKeys,
                    isDirection,
                    number,
                    currency_list
                ),
                customResponse = object : CustomResponse {
                    override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                        invoke(result)
                    }
                },
                context = context
            )
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
                    var edges: List<Storefront.ProductEdge>? = null
                    if (!getcategoryHandle().isEmpty()) {
                        edges = result.data!!.collectionByHandle.products.edges
                    }
                    if (!getcategoryID().isEmpty()) {
                        if (result.data!!.node != null) {
                            edges = (result.data?.node as Storefront.Collection).products.edges
                            collectionData.value = result.data?.node as Storefront.Collection
                        }
                    }
                    if (!shopID.isEmpty()) {
                        edges = result.data!!.products.edges
                    }
                    filterProduct(edges)
                }
            }
            Status.ERROR -> message.setValue(reponse.error!!.error.message)
            else -> {
            }
        }
    }

    fun filterProduct(list: List<Storefront.ProductEdge>?) {
        try {
            if (featuresModel.outOfStock!!) {
                disposables.add(repository.getProductList(list!!)
                    .subscribeOn(Schedulers.io())
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result -> filteredproducts.setValue(result) })
            } else {
                disposables.add(repository.getProductList(list!!)
                    .subscribeOn(Schedulers.io())
                    .filter { x -> x.node.availableForSale }
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result -> filteredproducts.setValue(result) })
            }

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

    override fun onCleared() {
        disposables.clear()
    }
    private fun getCollectionTags() {
        doRetrofitCall(repository.menuCollection(Urls(MyApplication.context)!!.mid,"tags"), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                collectionTags.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                collectionTags.value = ApiResponse.error(error)
            }
        }, context = context!!)
    }
    fun getFilterProducts(){
        disposables.add(repository.getCcollectionProductsbyTags(Urls(MyApplication.context)!!.mid
            , categoryHandle, "best-selling"
            ,"1", tags_)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> filerapiResponseData.setValue(ApiResponse.success(result)) },
                { throwable -> filerapiResponseData.setValue(ApiResponse.error(throwable)) }
            ))
    }
    fun ResponseApiFilterProducts(): MutableLiveData<ApiResponse> {
        getFilterProducts()
        return filerapiResponseData
    }
}
