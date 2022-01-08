package com.shopifydemodemo.app.personalised.viewmodels

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.personalised.adapters.PersonalisedAdapter
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.shopifyqueries.Query
import com.shopifydemodemo.app.utils.GraphQLResponse
import com.shopifydemodemo.app.utils.Status
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import java.nio.charset.Charset
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class PersonalisedViewModel(var repository: Repository) : ViewModel() {
    var activity: Activity? = null

    fun setPersonalisedData(
        data: JSONArray,
        adapter: PersonalisedAdapter,
        presentmentcurrency: String,
        recyler: RecyclerView
    ) {
        try {
            val edges = mutableListOf<Storefront.Product>()
            var currency_list = ArrayList<Storefront.CurrencyCode>()
            if (presentCurrency != "nopresentmentcurrency") {
                currency_list.add(Storefront.CurrencyCode.valueOf(presentCurrency!!))
            }
            var runnable = Runnable {
                for (i in 0..data.length() - 1) {
                    getProductById(
                        data.getJSONObject(i).getString("product_id"),
                        adapter,
                        presentmentcurrency,
                        recyler,
                        edges,
                        data,
                        currency_list
                    )
                }
            }
            Thread(runnable).start()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    val presentCurrency: String
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

    fun getProductById(
        id: String,
        adapter: PersonalisedAdapter,
        presentmentcurrency: String,
        recyler: RecyclerView,
        edges: MutableList<Storefront.Product>,
        data: JSONArray,
        currency_list: ArrayList<Storefront.CurrencyCode>
    ) {
        try {
            val call =
                repository.graphClient.queryGraph(Query.getProductById(getID(id), currency_list))
            call.enqueue(Handler(Looper.getMainLooper())) { result ->
                if (result is GraphCallResult.Success<*>) {
                    consumeResponse(
                        GraphQLResponse.success(result as GraphCallResult.Success<*>),
                        adapter,
                        presentmentcurrency,
                        recyler,
                        edges,
                        data
                    )
                } else {
                    consumeResponse(
                        GraphQLResponse.error(result as GraphCallResult.Failure),
                        adapter,
                        presentmentcurrency,
                        recyler,
                        edges,
                        data
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun consumeResponse(
        reponse: GraphQLResponse,
        adapter: PersonalisedAdapter,
        presentmentcurrency: String,
        recyler: RecyclerView,
        edges: MutableList<Storefront.Product>,
        data: JSONArray
    ) {
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
                    Log.i("MageNatyive", "ERROR" + errormessage.toString())
                    // message.setValue(errormessage.toString())
                } else {
                    try {
                        val edge = result.data!!.node as Storefront.Product
                        edges.add(edge)
                        if (edges.size == data.length()) {
                            filterProduct(edges, recyler, adapter, presentmentcurrency)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        when (MyApplication.context!!.getPackageName()) {
                            "com.shopifydemodemo.app" -> {
                                //Toast.makeText(MyApplication.context, "Please Provide Visibility to Products and Collections", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            Status.ERROR -> {
                Log.i("MageNatyive", "ERROR-1" + reponse.error!!.error.message)
            }
        }
    }

    private fun filterProduct(
        list: List<Storefront.Product>,
        productdata: RecyclerView?,
        adapter: PersonalisedAdapter,
        currency: String
    ) {
        try {
            repository.getProductListSlider(list)
                .subscribeOn(Schedulers.io())
                .filter { x -> x.availableForSale }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Storefront.Product>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onSuccess(list: List<Storefront.Product>) {
                        adapter!!.presentmentcurrency = currency
                        if (!adapter.hasObservers()) {
                            adapter!!.setHasStableIds(true)
                        }
                        adapter!!.setData(list, activity ?: Activity(), repository)
                        productdata!!.adapter = adapter
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getID(id: String): String {
        val data = Base64.encode(("gid://shopify/Product/" + id).toByteArray(), Base64.DEFAULT)
        return String(data, Charset.defaultCharset()).trim { it <= ' ' }
    }
}