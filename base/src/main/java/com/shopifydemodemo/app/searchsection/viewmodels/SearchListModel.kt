package com.shopifydemodemo.app.searchsection.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.shopifyqueries.Query
import com.shopifydemodemo.app.utils.GraphQLResponse
import com.shopifydemodemo.app.utils.Status
import java.util.concurrent.TimeUnit

import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class SearchListModel(private val repository: Repository) : ViewModel() {
    var presentmentcurrency: String? = null
    private val disposables = CompositeDisposable()
    private val responseLiveData = MutableLiveData<GraphQLResponse>()
    val message = MutableLiveData<String>()
    var filteredproducts: MutableLiveData<MutableList<Storefront.ProductEdge>>? = MutableLiveData<MutableList<Storefront.ProductEdge>>()
    private val avc = MutableLiveData<GraphQLResponse>()
    var searchcursor: String = "nocursor"

    private val mSearchResultsSubject: PublishSubject<String>

    init {
        mSearchResultsSubject = PublishSubject.create<String>()
        mSearchResultsSubject.debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map(Function<String, Unit> { o ->
                    Log.i("MageNative", "Search Keyword : $o")
                    getProductsByKeywords(o.toString())
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Unit> {
                    override fun onError(e: Throwable) {}
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(unit: Unit) {}
                })
    }

    fun setPresentmentCurrencyForModel() {
        try {
            val runnable = Runnable {
                if (repository.localData[0].currencycode == null) {
                    presentmentcurrency = "nopresentmentcurrency"
                } else {
                    presentmentcurrency = repository.localData[0].currencycode
                }
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    public fun getProductsByKeywords(keyword: String): Unit {
        var currency_list = ArrayList<Storefront.CurrencyCode>()
        if (presentmentcurrency != "nopresentmentcurrency") {
            currency_list.add(Storefront.CurrencyCode.valueOf(presentmentcurrency!!))
        }
        try {
            val call = repository.graphClient.queryGraph(Query.getSearchProducts(keyword, searchcursor, currency_list))
            call.enqueue(Handler(Looper.getMainLooper())) { result: GraphCallResult<Storefront.QueryRoot> -> this.invoke(result) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Unit
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
            if (SplashViewModel.featuresModel.outOfStock!!) {
                disposables.add(repository.getProductList(list)
                        .subscribeOn(Schedulers.io())
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { result ->
                            filteredproducts!!.value = result

                        })
            } else {
                disposables.add(repository.getProductList(list)
                        .subscribeOn(Schedulers.io())
                        .filter { x -> x.node.availableForSale }
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { result ->
                            filteredproducts!!.value = result

                        })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCleared() {
        disposables.clear()
    }

    fun setSearchData(data: String?) {
        mSearchResultsSubject.onNext(data!!)
    }

    fun searchResultforscanner(barcode: String) {
        var currency_list = ArrayList<Storefront.CurrencyCode>()
        if (presentmentcurrency != "nopresentmentcurrency") {
            currency_list.add(Storefront.CurrencyCode.valueOf(presentmentcurrency!!))
        }
        try {
            val call = repository.graphClient.queryGraph(Query.getProductByBarcode(barcode, currency_list))
            call.enqueue(Handler(Looper.getMainLooper())) { result: GraphCallResult<Storefront.QueryRoot> -> this.invoke(result) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
