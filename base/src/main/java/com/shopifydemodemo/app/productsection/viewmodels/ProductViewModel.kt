package com.shopifydemodemo.app.productsection.viewmodels

import android.content.Context
import android.util.Base64
import android.util.Log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonElement
import com.google.gson.JsonObject

import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.dbconnection.entities.CartItemData
import com.shopifydemodemo.app.dbconnection.entities.ItemData
import com.shopifydemodemo.app.dependecyinjection.Body
import com.shopifydemodemo.app.dependecyinjection.InnerData
import com.shopifydemodemo.app.network_transaction.CustomResponse
import com.shopifydemodemo.app.network_transaction.doGraphQLQueryGraph
import com.shopifydemodemo.app.network_transaction.doRetrofitCall
import com.shopifydemodemo.app.productsection.models.MediaModel
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.shopifyqueries.Query
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.GraphQLResponse
import com.shopifydemodemo.app.utils.Urls
import com.shopifydemodemo.app.utils.Urls.Data.SIZECHART
import java.util.concurrent.Callable
import java.util.concurrent.Executors

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import java.io.UnsupportedEncodingException
import java.lang.Runnable
import java.net.URL
import java.net.URLEncoder
import java.util.regex.Pattern

class ProductViewModel(private val repository: Repository) : ViewModel() {
    var handle = ""
    var id = ""
    var presentmentCurrency: String? = null
    private val disposables = CompositeDisposable()
    private val responseLiveData = MutableLiveData<GraphQLResponse>()
    val recommendedLiveData = MutableLiveData<GraphQLResponse>()
    var reviewResponse: MutableLiveData<ApiResponse>? = null
    var reviewBadges: MutableLiveData<ApiResponse>? = MutableLiveData<ApiResponse>()
    var createreviewResponse = MutableLiveData<ApiResponse>()
    var getjudgeMeProductID = MutableLiveData<ApiResponse>()
    var getjudgeMeReviewCount = MutableLiveData<ApiResponse>()
    var getjudgeMeReviewCreate = MutableLiveData<ApiResponse>()
    var getjudgeMeReviewIndex = MutableLiveData<ApiResponse>()
    var getAlireviewInstallStatus = MutableLiveData<ApiResponse>()
    var getAlireviewProduct = MutableLiveData<ApiResponse>()
    var sizeChartVisibility = MutableLiveData<Boolean>()
    var sizeChartUrl = MutableLiveData<String>()
    var getyotpocreate = MutableLiveData<ApiResponse>()
    lateinit var context: Context
    private val TAG = "ProductViewModel"
    val filteredlist = MutableLiveData<List<Storefront.ProductVariantEdge>>()


    fun getAliReviewStatus() {
        doRetrofitCall(repository.AliReviewInstallStatus(), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                getAlireviewInstallStatus.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                getAlireviewInstallStatus.value = ApiResponse.error(error)
            }
        }, context = context)
    }

    fun getAliReviewProduct(shop_id: String, product_id: String,currentPage:Int) {
        doRetrofitCall(repository.getAliProductReview(shop_id, product_id,currentPage), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                getAlireviewProduct.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                getAlireviewProduct.value = ApiResponse.error(error)
            }
        }, context = context)
    }

    fun judgemeProductID(url: String, handle: String, apiToken: String, shopDomain: String) {
        doRetrofitCall(repository.judgemeProductID(url, handle, apiToken, shopDomain), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                getjudgeMeProductID.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                getjudgeMeProductID.value = ApiResponse.error(error)
            }
        }, context = context)
    }

    fun judgemeReviewCount(product_id: String, apiToken: String, shopDomain: String) {
        doRetrofitCall(repository.judgemeReviewCount(product_id, apiToken, shopDomain), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                getjudgeMeReviewCount.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                getjudgeMeReviewCount.value = ApiResponse.error(error)
            }
        }, context = context)
    }

    fun judgemeReviewCreate(params: JsonObject) {
        doRetrofitCall(repository.judgemeReviewCreate(params), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                getjudgeMeReviewCreate.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                getjudgeMeReviewCreate.value = ApiResponse.error(error)
            }
        }, context = context)
    }

    fun judgemeReviewIndex(product_id: String, apiToken: String, shopDomain: String, per_page: Int, page: Int) {
        doRetrofitCall(repository.judgemeReviewIndex(apiToken, shopDomain, per_page, page, product_id), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                getjudgeMeReviewIndex.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                getjudgeMeReviewIndex.value = ApiResponse.error(error)
            }
        }, context = context)
    }

    fun getReviews(mid: String, product_id: String, page: Int): MutableLiveData<ApiResponse> {
        reviewResponse = MutableLiveData<ApiResponse>()
        getProductReviews(mid, product_id, page)
        return reviewResponse!!
    }

    fun getReviewBadges(mid: String, product_id: String): MutableLiveData<ApiResponse> {
        getbadgeReviews(mid, product_id)
        return reviewBadges!!
    }

    fun getProductReviews(mid: String, product_id: String, page: Int) {

        doRetrofitCall(repository.getProductReviews(mid, product_id, page), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                reviewResponse?.setValue(ApiResponse.success(result))
            }

            override fun onErrorRetrofit(error: Throwable) {
                reviewResponse?.setValue(ApiResponse.error(error))
            }
        }, context = context)

    }


    fun getbadgeReviews(mid: String, product_id: String) {

        doRetrofitCall(repository.getbadgeReviews(mid, product_id), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                reviewBadges?.setValue(ApiResponse.success(result))
            }

            override fun onErrorRetrofit(error: Throwable) {
                reviewBadges?.setValue(ApiResponse.error(error))
            }
        }, context = context)

    }

    fun getcreateReview(mid: String, reviewRating: String, product_id: String, reviewAuthor: String, reviewEmail: String, reviewTitle: String, reviewBody: String) {
        doRetrofitCall(repository.getcreateReview(mid, reviewRating, product_id, reviewAuthor, reviewEmail, reviewTitle, reviewBody), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                createreviewResponse.setValue(ApiResponse.success(result))
            }

            override fun onErrorRetrofit(error: Throwable) {
                createreviewResponse.setValue(ApiResponse.error(error))
            }
        }, context = context)
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

    fun shopifyRecommended() {
        var currency_list = ArrayList<Storefront.CurrencyCode>()
        if (presentmentCurrency != "nopresentmentcurrency") {
            currency_list.add(Storefront.CurrencyCode.valueOf(presentmentCurrency!!))
        }
        if (SplashViewModel.featuresModel.recommendedProducts) {
            getRecommendedProducts(currency_list)
        }
    }

    fun Response(): MutableLiveData<GraphQLResponse> {
        var currency_list = ArrayList<Storefront.CurrencyCode>()
        if (presentmentCurrency != "nopresentmentcurrency") {
            currency_list.add(Storefront.CurrencyCode.valueOf(presentmentCurrency!!))
        }
        if (!id.isEmpty()) {
            getProductsById(currency_list)
        }
        if (!handle.isEmpty()) {
            getProductsByHandle(currency_list)
        }
        return responseLiveData
    }

    private fun getRecommendedProducts(currencyList: ArrayList<Storefront.CurrencyCode>) {
        try {
            doGraphQLQueryGraph(repository, Query.recommendedProducts(id, currencyList), customResponse = object : CustomResponse {
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

    private fun getProductsById(currency_list: ArrayList<Storefront.CurrencyCode>) {
        try {
            doGraphQLQueryGraph(repository, Query.getProductById(id, currency_list), customResponse = object : CustomResponse {
                override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                    invoke(result)
                }
            }, context = context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getProductsByHandle(currency_list: ArrayList<Storefront.CurrencyCode>) {
        try {
            doGraphQLQueryGraph(repository, Query.getProductByHandle(handle, currency_list), customResponse = object : CustomResponse {
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
            responseLiveData.setValue(GraphQLResponse.success(result as GraphCallResult.Success<*>))
        } else {
            responseLiveData.setValue(GraphQLResponse.error(result as GraphCallResult.Failure))
        }
        return Unit
    }

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

    fun filterList(list: List<Storefront.ProductVariantEdge>) {
        try {
            disposables.add(repository.getList(list)
                    .subscribeOn(Schedulers.io())
                    //  .filter { x -> x.node.availableForSale }
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result -> filteredlist.setValue(result) })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCleared() {
        disposables.clear()
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

    fun addToCart(variantId: String, quantity: Int) {
        try {
            val runnable = Runnable {
                val data: CartItemData
                if (repository.getSingLeItem(variantId) == null) {
                    data = CartItemData()
                    data.variant_id = variantId
                    data.qty = quantity
                    repository.addSingLeItem(data)
                } else {
                    data = repository.getSingLeItem(variantId)
                    val qty = data.qty + quantity
                    data.qty = qty
                    repository.updateSingLeItem(data)
                }
                Log.i("MageNative", "CartCount : " + repository.allCartItems.size)
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getQtyInCart(variantId: String): Int {
        var variant_qty = runBlocking(Dispatchers.IO) {
            if (repository.getSingLeItem(variantId) == null) {
                return@runBlocking 0
            } else {
                return@runBlocking repository.getSingLeItem(variantId).qty
            }
        }
        return variant_qty
    }

    private val api = MutableLiveData<ApiResponse>()
    fun getApiResponse(): MutableLiveData<ApiResponse> {
        return api
    }

    fun getRecommendations(id: String) {
        RetrofitUrlManager.getInstance().putDomain("douban", Urls.PERSONALISED);
        try {
            var query = InnerData()
            query.id = "query1"
            query.maxRecommendations = 8
            query.recommendationType = "similar_products"
            var list = mutableListOf<Long>()
            var s = String(Base64.decode(id, Base64.DEFAULT))
            list.add(s.replace("gid://shopify/Product/", "").toLong())
            query.productIds = list
            var body = Body()
            body.queries = mutableListOf(query)
            Log.i("Body", "" + list)
            doRetrofitCall(repository.getRecommendation(body), disposables, customResponse = object : CustomResponse {
                override fun onSuccessRetrofit(result: JsonElement) {
                    api.setValue(ApiResponse.success(result))
                }

                override fun onErrorRetrofit(error: Throwable) {
                    api.setValue(ApiResponse.error(error))
                }
            }, context = context)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSizeChart(shop: String, source: String, product_id: String, tags: String, vendor: String, collections: String? = null) {
        //   Log.d(TAG, "getSizeChart: "+collections)
        RetrofitUrlManager.getInstance().putDomain("douban", Urls.SIZECHART)
        var hashMap = HashMap<String, String>()
        hashMap.put("shop", shop)
        hashMap.put("source", source)
        hashMap.put("product", product_id)
        hashMap.put("tags", tags)
        hashMap.put("vendor", vendor)
        if (collections != null) {
            hashMap.put("collections", collections)
        }
        Log.d("OKHttp", "" + SIZECHART + "?" + getPostDataString(hashMap))
        sizeChartUrl.value = SIZECHART + "?" + getPostDataString(hashMap)
        GlobalScope.launch(Dispatchers.Main) {
            try {
                coroutineScope {
                    var result = async(Dispatchers.IO) {
                        URL(SIZECHART + "?" + getPostDataString(hashMap)).readText()
                    }
                    parseResponse(result.await())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseResponse(await: String) {
        if (await.length == 0) {
            sizeChartVisibility.value = false
        } else {
            sizeChartVisibility.value = true
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getPostDataString(params: HashMap<String, String>): String? {
        val result = StringBuilder()
        var first = true
        for ((key, value) in params.entries) {
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }
        Log.i("POST_STRING", "" + result)
        return result.toString()
    }

    fun filterArModel(armodelList: MutableList<MediaModel>): MutableLiveData<MutableList<MediaModel>> {
        val ardatamodelList = MutableLiveData<MutableList<MediaModel>>()
        disposables.add(repository.getArModels(armodelList).subscribeOn(Schedulers.io())
                .filter { t -> t.typeName.equals("Model3d") }
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe { result -> ardatamodelList.value = result }
        )
        return ardatamodelList
    }

    fun isValidEmail(target: String): Boolean {
        val emailPattern = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE)
        return emailPattern.matcher(target).matches()
    }

    fun NResponse(appkey: String, sku: String, product_title: String, product_url: String, display_name: String, email: String, review_content: String, review_title: String, review_score: String): MutableLiveData<ApiResponse> {
        yotpocretereview(appkey, sku, product_title,product_url,display_name,email,review_content,review_title,review_score)
        return getyotpocreate
    }

    fun yotpocretereview(appkey: String, sku: String, product_title: String, product_url: String, display_name: String, email: String, review_content: String, review_title: String, review_score: String) {
        doRetrofitCall(repository.yotpocretereview(appkey, sku, product_title,product_url,display_name,email,review_content,review_title,review_score), disposables, customResponse = object : CustomResponse {
            override fun onSuccessRetrofit(result: JsonElement) {
                getyotpocreate.value = ApiResponse.success(result)
            }

            override fun onErrorRetrofit(error: Throwable) {
                getyotpocreate.value = ApiResponse.error(error)
            }
        }, context = context)
    }
}
