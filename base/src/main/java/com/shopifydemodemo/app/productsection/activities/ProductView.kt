package com.shopifydemodemo.app.productsection.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopify.graphql.support.ID
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.activities.Weblink
import com.shopifydemodemo.app.basesection.models.ListData
import com.shopifydemodemo.app.basesection.viewmodels.LeftMenuViewModel
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel.Companion.featuresModel
import com.shopifydemodemo.app.cartsection.activities.CartList
import com.shopifydemodemo.app.databinding.*
import com.shopifydemodemo.app.personalised.adapters.PersonalisedAdapter
import com.shopifydemodemo.app.personalised.viewmodels.PersonalisedViewModel
import com.shopifydemodemo.app.productsection.adapters.*
import com.shopifydemodemo.app.productsection.models.MediaModel
import com.shopifydemodemo.app.productsection.models.Review
import com.shopifydemodemo.app.productsection.models.ReviewModel
import com.shopifydemodemo.app.productsection.viewmodels.ProductViewModel
import com.shopifydemodemo.app.sharedprefsection.MagePrefs
import com.shopifydemodemo.app.utils.*
import kotlinx.android.synthetic.main.m_addressitem.*
import kotlinx.android.synthetic.main.m_newbaseactivity.*
import kotlinx.android.synthetic.main.m_productview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import android.webkit.WebSettings


class ProductView : NewBaseActivity() {
    private var product_handle: String? = null
    private var productName: String? = null
    private var productsku: String? = null
    private var binding: MProductviewBinding? = null

    @Inject
    lateinit var factory: ViewModelFactory
    private var model: ProductViewModel? = null
    private val TAG = "ProductView"
    var productID = "noid"
    var whishlistArray = JSONArray()
    var cartlistArray = JSONArray()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var variantId: ID? = null
    var sizeChartUrl: String = ""
    private var singleVariant: Boolean = false
    private var variantEdge: Storefront.ProductVariant? = null

    @Inject
    lateinit var reviewAdapter: ReviewListAdapter

    lateinit var adapter: VariantAdapter
    private var data: ListData? = null
    private var personamodel: PersonalisedViewModel? = null
    private var inStock: Boolean = true
    private var totalVariant: Int? = null

    //private var variantValidation: JSONObject = JSONObject()
    private var variantValidation: MutableMap<String, String> = mutableMapOf()
    var reviewModel: ReviewModel? = null
    private var external_id: String? = null
    private var judgeme_productid: String? = null
    private var AliProductId: String? = null
    private var AliShopId: String? = null
    private var reviewList: ArrayList<Review>? = null
    private var mediaList = mutableListOf<MediaModel>()
    protected lateinit var leftmenu: LeftMenuViewModel

    @Inject
    lateinit var arImagesAdapter: ArImagesAdapter

    @Inject
    lateinit var customadapter: CustomAdapters

    @Inject
    lateinit var personalisedadapter: PersonalisedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constant.previous = null
        Constant.current = null
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_productview, group, true)
        binding?.features = featuresModel
        showBackButton()
        leftmenu = ViewModelProvider(this, viewModelFactory).get(LeftMenuViewModel::class.java)
        (application as MyApplication).mageNativeAppComponent!!.doProductViewInjection(this)
        model = ViewModelProvider(this, factory).get(ProductViewModel::class.java)
        model!!.context = this
        firebaseAnalytics = Firebase.analytics
        model?.createreviewResponse?.observe(this, Observer { this.createReview(it) })
        personamodel = ViewModelProvider(this, factory).get(PersonalisedViewModel::class.java)
        personamodel?.activity = this
        if (intent.getStringExtra("handle") != null) {
            model!!.handle = intent.getStringExtra("handle")!!
        }
        if (intent.getStringExtra("ID") != null) {
            model!!.id = intent.getStringExtra("ID")!!
            productID = model!!.id
        }
        Log.d(TAG, "onCreate: " + getBase64Decode(productID)!!)
        Log.i("PID", "" + productID)
        if (featuresModel.productReview!!) {
            model?.getReviewBadges(
                Urls(application as MyApplication).mid,
                getBase64Decode(productID)!!
            )?.observe(this, Observer { this.consumeBadges(it) })
            model?.getReviews(
                Urls(application as MyApplication).mid,
                getBase64Decode(productID)!!,
                1
            )?.observe(this, Observer { this.consumeReview(it) })
            binding?.reviewCard?.visibility = View.VISIBLE
        }
        if (featuresModel.sizeChartVisibility) {
            model?.sizeChartVisibility?.observe(
                this,
                Observer { this.consumeSizeChartVisibility(it) })
            model?.sizeChartUrl?.observe(this, Observer { this.consumeSizeChartURL(it) })
        }
        if (featuresModel.aliReviews) {
            model?.getAlireviewInstallStatus?.observe(
                this,
                Observer { this.consumeAliReviewStatus(it) })
            model?.getAlireviewProduct?.observe(this, Observer { this.consumeAliReviews(it) })
            model?.getAliReviewStatus()
        }

        data = ListData()
        if (model!!.setPresentmentCurrencyForModel()) {
            //  model!!.filteredlist.observe(this, Observer<List<Storefront.ProductVariantEdge>> { this.filterResponse(it) })
            if (featuresModel.ai_product_reccomendaton) {
                model!!.getApiResponse()
                    .observe(this, Observer<ApiResponse> { this.consumeResponse(it) })
            }
            if (intent.getSerializableExtra("product") != null) {
                setProductData(intent.getSerializableExtra("product") as Storefront.Product)
            } else {
                model!!.Response()
                    .observe(this, Observer<GraphQLResponse> { this.consumeResponse(it) })
            }
        }

        model!!.recommendedLiveData.observe(this, Observer { this.consumeRecommended(it) })
        model!!.shopifyRecommended()
        if (featuresModel.judgemeProductReview) {
            model?.getjudgeMeProductID?.observe(this, Observer { this.consumeJudgeMeProductID(it) })
            model?.getjudgeMeReviewCount?.observe(
                this,
                Observer { this.consumeJudgeMeReviewCount(it) })
            model?.getjudgeMeReviewIndex?.observe(this, Observer { this.consumeJudgeMeReview(it) })
        }
        binding?.variantAvailableQty?.textSize = 14f
        binding?.qtyTitleTxt?.textSize = 14f
        binding?.yotpoWriteReviewBut?.setOnClickListener {
            if (yotporeviewsection.visibility == View.VISIBLE) {
                yotporeviewsection.visibility = View.GONE
            } else {
                yotporeviewsection.visibility = View.VISIBLE
            }
        }
        binding?.yotpoSubmitreview?.setOnClickListener {
            if (binding!!.yotpoName.text!!.toString().isEmpty()) {
                binding!!.yotpoName.error = resources.getString(R.string.empty)
                binding!!.yotpoName.requestFocus()
            } else if (binding!!.yotpoEmail.text!!.toString().isEmpty()) {
                binding!!.yotpoEmail.error = resources.getString(R.string.empty)
                binding!!.yotpoEmail.requestFocus()
            } else if (binding!!.yotpoReviewtitle.text!!.toString().isEmpty()) {
                binding!!.yotpoReviewtitle.error = resources.getString(R.string.empty)
                binding!!.yotpoReviewtitle.requestFocus()
            } else if (binding!!.yotpoReviewbody.text!!.toString().isEmpty()) {
                binding!!.yotpoReviewbody.error = resources.getString(R.string.empty)
                binding!!.yotpoReviewbody.requestFocus()
            } else {
                if (leftmenu.isLoggedIn) {
                    submityptporeview()
                } else {
                    val alertDialog: AlertDialog = AlertDialog.Builder(this@ProductView).create()
                    alertDialog.setTitle("NOTE!")
                    alertDialog.setMessage("Please create an account in the app to leave a review.")
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                    alertDialog.show()
                }
            }
        }
    }

    private fun consumeAliReviews(response: ApiResponse?) {
        Log.d(TAG, "consumeAliReviews: " + response?.data)
        var responseData = JSONObject(response?.data.toString())
        if (responseData.getBoolean("status")) {
            binding?.aliReviewSection?.visibility = View.VISIBLE
            binding?.reviewCard?.visibility = View.VISIBLE
            binding?.ratingTxt?.text = responseData.getString("avg")
            binding?.totalReview?.text = responseData.getString("total_review")

            var reviews = responseData.getJSONObject("data").getJSONArray("data")
            reviewList = ArrayList<Review>()
            var review_model: Review? = null
            for (i in 0 until reviews.length()) {
                review_model = Review(
                    reviews.getJSONObject(i).getString("content"),
                    reviews.getJSONObject(i).getString("id"),
                    reviews.getJSONObject(i).getString("star"),
                    reviews.getJSONObject(i).getString("star"),
                    reviews.getJSONObject(i).getString("created_at"),
                    reviews.getJSONObject(i).getString("author"),
                    ""
                    /*  reviews.getJSONObject(i).getString("title")*/
                )
                reviewList?.add(review_model)
            }
            if (reviewList?.size!! > 0) {
                binding?.aliNoReviews?.visibility = View.GONE
                binding?.aliReviewList?.visibility = View.VISIBLE
                binding?.aliViewAllBut?.visibility = View.VISIBLE
                binding?.aliRateProductBut?.visibility = View.GONE
                binding?.aliReviewIndecator?.visibility = View.VISIBLE
                reviewAdapter = ReviewListAdapter()
                reviewAdapter.setData(reviewList)
                binding?.aliReviewList?.adapter = reviewAdapter
                binding?.aliReviewIndecator?.tintIndicator(Color.parseColor(themeColor))
                binding?.aliReviewIndecator?.setViewPager(binding?.aliReviewList)
            } else {
                binding?.aliNoReviews?.visibility = View.VISIBLE
                binding?.aliReviewList?.visibility = View.GONE
                binding?.aliRateProductBut?.visibility = View.GONE
                binding?.aliReviewIndecator?.visibility = View.GONE
                binding?.aliViewAllBut?.visibility = View.GONE
            }
        } else {
            binding?.aliReviewSection?.visibility = View.GONE
            binding?.reviewCard?.visibility = View.GONE
            binding?.aliNoReviews?.visibility = View.GONE
            binding?.aliReviewList?.visibility = View.GONE
            binding?.aliRateProductBut?.visibility = View.GONE
            binding?.aliReviewIndecator?.visibility = View.GONE
            binding?.aliViewAllBut?.visibility = View.GONE
        }
    }

    private fun consumeAliReviewStatus(response: ApiResponse?) {
        Log.d(TAG, "consumeAliReviewStatus: " + response?.data)
        try {
            var responseData = JSONObject(response?.data.toString())
            if (responseData.get("status") is String && responseData.get("status")
                    .equals("error")
            ) {
                //  Toast.makeText(this,responseData.getString("message"),Toast.LENGTH_SHORT).show()
                binding?.aliReviewSection?.visibility = View.GONE
            } else {
                if (responseData.getBoolean("status")) {
                    AliProductId = getBase64Decode(productID)!!
                    AliShopId = responseData.getJSONObject("result").getString("shop_id")
                    model?.getAliReviewProduct(
                        responseData.getJSONObject("result").getString("shop_id"),
                        getBase64Decode(productID)!!,
                        1
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun consumeJudgeMeReview(response: ApiResponse?) {
        Log.d(TAG, "consumeJudgeMeReview: " + response?.data)
        var responseData = JSONObject(response?.data.toString())
        var reviews = responseData.getJSONArray("reviews")
        reviewList = ArrayList<Review>()
        var review_model: Review? = null
        for (i in 0 until reviews.length()) {
            review_model = Review(
                reviews.getJSONObject(i).getString("body"),
                reviews.getJSONObject(i).getString("id"),
                reviews.getJSONObject(i).getString("rating"),
                reviews.getJSONObject(i).getString("rating"),
                reviews.getJSONObject(i).getString("created_at"),
                reviews.getJSONObject(i).getJSONObject("reviewer").getString("name"),
                reviews.getJSONObject(i).getString("title")
            )
            reviewList?.add(review_model)
        }

        if (reviewList?.size!! > 0) {
            binding?.judgemeNoReviews?.visibility = View.GONE
            binding?.judgemeReviewList?.visibility = View.VISIBLE
            binding?.judgemeViewAllBut?.visibility = View.VISIBLE
            binding?.judgemeReviewIndecator?.visibility = View.VISIBLE
            reviewAdapter = ReviewListAdapter()
            reviewAdapter.setData(reviewList)
            binding?.judgemeReviewList?.adapter = reviewAdapter
            binding?.judgemeReviewIndecator?.tintIndicator(Color.parseColor(themeColor))
            binding?.judgemeReviewIndecator?.setViewPager(binding?.judgemeReviewList)
        } else {
            binding?.judgemeNoReviews?.visibility = View.VISIBLE
            binding?.judgemeReviewList?.visibility = View.GONE
            binding?.judgemeReviewIndecator?.visibility = View.GONE
            binding?.judgemeViewAllBut?.visibility = View.GONE
        }
    }

    private fun consumeJudgeMeReviewCount(response: ApiResponse?) {
        Log.d(TAG, "consumeJudgeMeReviewCount: " + response?.data)
        binding?.judgemeReviewCardSection?.visibility = View.VISIBLE
        binding?.judgemeRatingTxt?.text =
            JSONObject(response?.data.toString()).getString("count") + " " + getString(R.string.reviews)
    }

    private fun consumeJudgeMeProductID(response: ApiResponse?) {
        Log.d(TAG, "consumeJudgeMeProductID: " + response?.data)
        if (response?.data != null) {
            var responseData = JSONObject(response.data.toString())
            if (responseData.has("product")) {
                var product = responseData.getJSONObject("product")
                model?.judgemeReviewCount(
                    product.getString("id"),
                    Urls.JUDGEME_APITOKEN,
                    Urls(application as MyApplication).shopdomain
                )
                model?.judgemeReviewIndex(
                    product.getString("id"),
                    Urls.JUDGEME_APITOKEN,
                    Urls(application as MyApplication).shopdomain,
                    5,
                    1
                )
                external_id = product.getString("external_id")
                judgeme_productid = product.getString("id")
            }
        }
    }

    private fun consumeRecommended(reponse: GraphQLResponse?) {
        when (reponse?.status) {
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
                    Toast.makeText(this, "" + errormessage, Toast.LENGTH_SHORT).show()
                } else {
                    var recommendedList =
                        result.data!!.productRecommendations as ArrayList<Storefront.Product>?
                    if (recommendedList?.size!! > 0) {
                        Log.d(TAG, "consumeRecommended: " + recommendedList.size)
                        binding!!.shopifyrecommendedSection.visibility = View.VISIBLE
                        setLayout(binding!!.shopifyrecommendedList, "horizontal")
                        personalisedadapter = PersonalisedAdapter()
                        personalisedadapter.setData(
                            recommendedList,
                            this,
                            personamodel?.repository!!
                        )
                        binding!!.shopifyrecommendedList.adapter = personalisedadapter
                    }
                }
            }
            Status.ERROR -> Toast.makeText(this, reponse.error!!.error.message, Toast.LENGTH_SHORT)
                .show()
            else -> {
            }
        }
    }

    private fun consumeSizeChartURL(it: String?) {
        sizeChartUrl = it!!
    }

    private fun consumeSizeChartVisibility(it: Boolean?) {
        if (it!!) {
            binding?.sizeChartSection?.visibility = View.VISIBLE
        } else {
            binding?.sizeChartSection?.visibility = View.GONE
        }
    }

    private fun createReview(response: ApiResponse?) {
        if (response?.data != null) {
            var data = JSONObject(response.data.toString())
            if (data.getBoolean("success")) {
                Toast.makeText(this, getString(R.string.review_submitted), Toast.LENGTH_SHORT)
                    .show()
                GlobalScope.launch(Dispatchers.Main) {
                    delay(2000)
                    model?.getProductReviews(
                        Urls(application as MyApplication).mid,
                        getBase64Decode(productID)!!,
                        1
                    )
                    model?.getbadgeReviews(
                        Urls(application as MyApplication).mid,
                        getBase64Decode(productID)!!
                    )
                }
            }
        }
    }

    private fun consumeBadges(response: ApiResponse?) {
        if (response?.data != null) {
            var data = JSONObject(response.data.toString()).getJSONObject("data")
            binding?.ratingTxt?.text =
                data.getJSONObject(getBase64Decode(productID)).getString("total-rating")
                    .substring(0, 3)
            binding?.totalReview?.text =
                data.getJSONObject(getBase64Decode(productID)).getString("total-reviews")
        }
    }

    private fun consumeReview(response: ApiResponse?) {
        if (response?.data != null) {
            try {
                Log.d(TAG, "consumeReview: " + JSONObject(response.data.toString()))
                if (JSONObject(response.data.toString()).getJSONObject("data").has("reviews")) {
                    reviewModel = Gson().fromJson<ReviewModel>(
                        response.data.toString(),
                        ReviewModel::class.java
                    ) as ReviewModel
                    if (reviewModel?.success!!) {
                        if (reviewModel?.data?.reviews?.size!! > 0) {
                            binding?.noReviews?.visibility = View.GONE
                            binding?.reviewList?.visibility = View.VISIBLE
                            binding?.viewAllBut?.visibility = View.VISIBLE
                            binding?.reviewIndecator?.visibility = View.VISIBLE
                            reviewAdapter.setData(reviewModel?.data?.reviews)
                            binding?.reviewList?.adapter = reviewAdapter
                            binding?.reviewIndecator?.tintIndicator(Color.parseColor(themeColor))
                            binding?.reviewIndecator?.setViewPager(binding?.reviewList)
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                binding?.noReviews?.visibility = View.VISIBLE
                binding?.reviewList?.visibility = View.GONE
                binding?.reviewIndecator?.visibility = View.GONE
                binding?.viewAllBut?.visibility = View.GONE
            }
        }
    }

    fun getBase64Decode(id: String?): String? {
        val data = Base64.decode(id, Base64.DEFAULT)
        var text = String(data, StandardCharsets.UTF_8)
        val datavalue = text.split("/".toRegex()).toTypedArray()
        val valueid = datavalue[datavalue.size - 1]
        val datavalue2 = valueid.split("key".toRegex()).toTypedArray()
        text = datavalue2[0]
        return text
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
                    Toast.makeText(this, "" + errormessage, Toast.LENGTH_SHORT).show()
                } else {
                    var productedge: Storefront.Product? = null
                    if (!model!!.handle.isEmpty()) {
                        productedge = result.data!!.productByHandle
                    }
                    if (!model!!.id.isEmpty()) {
                        productedge = result.data!!.node as Storefront.Product
                    }
                    Log.i("MageNative", "Product_id" + productedge!!.id.toString())
                    setProductData(productedge)
                }
            }
            Status.ERROR -> Toast.makeText(this, reponse.error!!.error.message, Toast.LENGTH_SHORT)
                .show()
            else -> {
            }
        }
    }

    private fun consumeResponse(reponse: ApiResponse) {
        when (reponse.status) {
            Status.SUCCESS -> setPersonalisedData(reponse.data!!)
            Status.ERROR -> {
                reponse.error!!.printStackTrace()
                //Toast.makeText(this, resources.getString(R.string.errorString), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setPersonalisedData(data: JsonElement) {
        try {
            val jsondata = JSONObject(data.toString())
            if (jsondata.has("query1")) {
                binding!!.personalisedsection.visibility = View.VISIBLE
                setLayout(binding!!.personalised, "horizontal")
                personamodel!!.setPersonalisedData(
                    jsondata.getJSONObject("query1").getJSONArray("products"),
                    personalisedadapter,
                    model!!.presentmentCurrency!!,
                    binding!!.personalised
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n", "LogNotTimber", "SetJavaScriptEnabled")
    private fun setProductData(productedge: Storefront.Product?) {
        try {
            var mediaModel: MediaModel? = null
            for (i in 0..productedge!!.media.edges.size - 1) {
                var a: String = productedge.media.edges.get(i).node.graphQlTypeName
                if (a.equals("Model3d")) {
                    var d = productedge.media.edges.get(i).node as Storefront.Model3d
                    if (d.sources.get(0).url.contains(".glb")) {
                        data!!.arimage = d.sources.get(0).url
                        mediaModel = MediaModel(
                            d.graphQlTypeName,
                            d.previewImage.originalSrc,
                            d.sources.get(0).url
                        )
                        mediaList.add(mediaModel)
                        if (featuresModel.ardumented_reality) {
                            binding!!.aricon.visibility = View.VISIBLE
                        } else {
                            binding!!.aricon.visibility = View.GONE
                        }
                    }
                } else if (a.equals("Video")) {
                    val video = productedge.media.edges.get(i).node as Storefront.Video
                    mediaModel = MediaModel(
                        video.graphQlTypeName,
                        video.previewImage.originalSrc,
                        video.sources.get(0).url
                    )
                    mediaList.add(mediaModel)
                } else if (a.equals("ExternalVideo")) {
                    val externalVideo =
                        productedge.media.edges.get(i).node as Storefront.ExternalVideo
                    mediaModel = MediaModel(
                        externalVideo.graphQlTypeName,
                        externalVideo.previewImage.originalSrc,
                        externalVideo.embeddedUrl
                    )
                    mediaList.add(mediaModel)
                } else if (a.equals("MediaImage")) {
                    var mediaImage = productedge.media.edges.get(i).node as Storefront.MediaImage
                    mediaModel = MediaModel(
                        mediaImage.graphQlTypeName,
                        mediaImage.previewImage.originalSrc,
                        ""
                    )
                    mediaList.add(mediaModel)
                }
            }
            Log.d(TAG, "setProductData: " + mediaList)
            Log.d(TAG, "setProductData: " + productedge.handle)
            product_handle = productedge.handle
            if (featuresModel.judgemeProductReview) {
                model?.judgemeProductID(
                    Urls.JUDGEME_GETPRODUCTID + productedge.handle,
                    productedge.handle,
                    Urls.JUDGEME_APITOKEN,
                    Urls(application as MyApplication).shopdomain
                )
            }
            Log.d(TAG, "setProductData: " + productedge.id)
            var tags_data: StringBuilder = StringBuilder()
            if (productedge.tags.size > 0) {
                productedge.tags.forEach {
                    tags_data.append("$it,")
                }
                Log.d(TAG, "setProductData: " + tags_data.substring(0, tags_data.length - 1))
            } else {
                tags_data.append("")
            }
            if (featuresModel.sizeChartVisibility) {
                var collections: String? = null
                if (productedge.collections != null) {
                    if (productedge.collections?.edges?.size!! > 0) {
                        var buffer = StringBuffer()
                        for (i in 0 until productedge.collections.edges.size) {
                            buffer.append(getBase64Decode(productedge.collections.edges.get(i).node.id.toString()))
                                .append(",")
                        }
                        collections = buffer.substring(0, buffer.length - 1)
                    } else {
                        collections =
                            getBase64Decode(productedge.collections.edges.get(0).node.id.toString())
                    }
                }
                model!!.getSizeChart(
                    Urls(application as MyApplication).shopdomain,
                    "magenative",
                    getBase64Decode(productID)!!,
                    tags_data.toString(),
                    productedge.vendor,
                    collections
                )
            }

            if (Constant.ispersonalisedEnable) {
                model!!.getRecommendations(productedge.id.toString())
            }

            if (featuresModel.outOfStock!!) {
                if (!productedge.availableForSale) {
                    binding?.outOfStock?.visibility = View.VISIBLE
                    binding?.shareicon?.visibility = View.GONE
                    inStock = false
                } else {
                    inStock = true
                    binding?.outOfStock?.visibility = View.GONE
                    binding?.shareicon?.visibility = View.VISIBLE
                }
            }
            binding?.availableQty?.textSize = 14f
            binding?.availableQty?.text =
                getString(R.string.avaibale_qty) + " " + productedge.totalInventory
            val variant = productedge.variants.edges[0].node

            /******************************** Local delivery and pickup work **************************************/

            if (featuresModel.localpickupEnable) {
                val alledges = variant.storeAvailability.edges
                Log.d(TAG, alledges.size.toString())
                if (alledges.size > 1) {
                    binding?.checkstoretext?.visibility = View.VISIBLE
                } else {
                    binding?.checkstoretext?.visibility = View.GONE
                }
                if (variant.storeAvailability.edges[0].node.available.toString().equals("true")) {
                    binding?.card?.visibility = View.VISIBLE
                    binding?.heading?.text = getString(R.string.pickupavailable)
                    binding?.checks?.setImageResource(R.drawable.checkmark)
                    binding?.addfirst?.text =
                        variant.storeAvailability.edges[0].node.location.address.city
                    binding?.addsecond?.text =
                        variant.storeAvailability.edges[0].node.location.address.address2 + " " + variant.storeAvailability.edges[0].node.location.address.address1 + " " +
                                variant.storeAvailability.edges[0].node.location.address.province + " " + variant.storeAvailability.edges[0].node.location.address.city + " " + variant.storeAvailability.edges[0].node.location.address.zip
                    binding?.pickuptime?.text = variant.storeAvailability.edges[0].node.pickUpTime
                    binding?.phonenumber?.text =
                        variant.storeAvailability.edges[0].node.location.address.phone
                } else if (variant.storeAvailability.edges[0].node.available.toString()
                        .equals("false")
                ) {
                    binding?.card?.visibility = View.VISIBLE
                    binding?.heading?.text = getString(R.string.pickupavailablenot)
                    binding?.checks?.setImageResource(R.drawable.cross)
                    binding?.addfirst?.text =
                        variant.storeAvailability.edges[0].node.location.address.city
                    binding?.addsecond?.text =
                        variant.storeAvailability.edges[0].node.location.address.address2 + " " + variant.storeAvailability.edges[0].node.location.address.address1 + " " +
                                variant.storeAvailability.edges[0].node.location.address.province + " " + variant.storeAvailability.edges[0].node.location.address.city + " " + variant.storeAvailability.edges[0].node.location.address.zip
                    binding?.pickuptime?.text = variant.storeAvailability.edges[0].node.pickUpTime
                    binding?.phonenumber?.text =
                        variant.storeAvailability.edges[0].node.location.address.phone
                }
                binding?.storerecycler?.setHasFixedSize(true)
                binding?.storerecycler?.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                binding?.storerecycler?.isNestedScrollingEnabled = false
                customadapter.setData(alledges, this)
                binding?.storerecycler!!.adapter = customadapter
                binding?.checkstoretext?.setOnClickListener {
                    if (storerecycler.visibility == View.VISIBLE) {
                        storerecycler.visibility = View.GONE
                    } else {
                        storerecycler.visibility = View.VISIBLE
                    }
                }
            }
            /***************************************************************************************************/

            val slider = ImagSlider(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
            if (mediaList.size > 0) {
                slider.setData(mediaList)
            }
            data!!.product = productedge
            binding!!.images.adapter = slider
            binding!!.indicator.setViewPager(binding!!.images)
            data!!.textdata = productedge.title
            productName = productedge.title
            productsku = productedge.variants.edges[0].node.sku
            Log.i("ALLSHUUUUUUU", "" + productsku)
            showTittle(productName!!)
            Log.i("here", productedge.descriptionHtml)
            if (MagePrefs.getLanguage() == "ar") {
                val pish =
                    "<head><style>@font-face {font-family: 'arial';src: url('file:///android_asset/fonts/cairobold.ttf');}</style></head>"
                var desc =
                    "<html>" + pish + "<body style='font-family: arial'> <div dir='rtl'>" + productedge.descriptionHtml + "</div></body></html>"
                binding?.description?.loadDataWithBaseURL(
                    null,
                    desc,
                    "text/html",
                    "utf-8",
                    null
                )
            } else {
                val pish =
                    "<head><style>@font-face {font-family: 'arial';src: url('file:///android_asset/fonts/cairobold.ttf');}</style></head>"
                var desc =
                    "<html>" + pish + "<body style='font-family: arial'>" + productedge.descriptionHtml + "</body></html>"
                binding?.description?.loadDataWithBaseURL(
                    null,
                    desc,
                    "text/html",
                    "utf-8",
                    null
                )
            }
            binding?.description?.settings?.loadWithOverviewMode = true
            binding?.description?.settings?.useWideViewPort = true
            binding?.description?.settings?.javaScriptEnabled = true
            val webSettings: WebSettings? = binding?.description?.settings
            webSettings?.defaultFontSize = 30
            if (model?.isInwishList(model?.id!!)!!) {
                data!!.addtowish = resources.getString(R.string.alreadyinwish)
                Glide.with(this).load(R.drawable.wishlist_selected)
                    .into(binding?.addtowish!!)
            } else {
                data!!.addtowish = resources.getString(R.string.addtowish)
                Glide.with(this).load(R.drawable.wishlist_icon)
                    .into(binding?.addtowish!!)
            }
            var contentViewArray = JSONArray()
            var cartlistData = JSONObject()
            cartlistData.put("id", productedge.id.toString())
            cartlistData.put("quantity", 1)
            contentViewArray.put(cartlistData.toString())
            Constant.logViewContentEvent(
                "product",
                contentViewArray.toString(),
                productedge.id.toString(),
                productedge.variants.edges.get(0).node.presentmentPrices.edges.get(0).node.price.currencyCode.toString(),
                productedge.variants.edges.get(0).node.presentmentPrices.edges.get(0).node.price.amount.toDouble(),
                this
            )
            setProductPrice(variant)
            binding?.regularprice?.textSize = 15f
            //  model!!.filterList(productedge.variants.edges)
            filterOptionList(productedge.options, productedge.variants.edges)
            binding!!.productdata = data
            binding!!.clickhandlers = ClickHandlers()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun filterOptionList(
        options: List<Storefront.ProductOption>,
        edges: MutableList<Storefront.ProductVariantEdge>
    ) {
        Log.d(TAG, "filterOptionList: " + options)
        var swatechView: SwatchesListBinding? = null
        var outofStockList: MutableList<String> = mutableListOf()
        if (edges.size > 1) {
            binding!!.variantheading.visibility = View.VISIBLE
            binding!!.variantContainer.visibility = View.VISIBLE
        } else {
            binding!!.variantheading.visibility = View.GONE
            binding!!.variantContainer.visibility = View.GONE
            binding?.variantAvailableQty?.visibility = View.GONE
            singleVariant = true
            variantId = edges.get(0).node.id
            variantValidation.put("title", variantId.toString())
            binding?.variantAvailableQty?.text =
                edges.get(0).node.quantityAvailable.toString() + " " + resources.getString(R.string.avaibale_qty_variant)
            setProductPrice(edges.get(0).node)
        }
        for (i in 0 until edges.size) {
            if (!edges.get(i).node.availableForSale) {
                outofStockList.add(edges.get(i).node.title)
            }
        }
        totalVariant = options.size
        var variant_pair: MutableMap<String, String> = mutableMapOf()
        for (j in 0 until options.size) {
            swatechView =
                DataBindingUtil.inflate(layoutInflater, R.layout.swatches_list, null, false)
            swatechView.variantTitle.text = options.get(j).name
            adapter = VariantAdapter()
            adapter.setData(
                options.get(j).name,
                options.get(j).values,
                outofStockList,
                this,
                object : VariantAdapter.VariantCallback {
                    override fun clickVariant(variantName: String, optionName: String) {
                        variant_pair.put(optionName, variantName)
                        if (totalVariant == variant_pair.size) {
                            variantFilter(variant_pair.values.toList(), edges, variant_pair)
                        }
                        //variantValidation.put(variantName, options.get(j).id.toString())
                    }
                })
            swatechView.variantList.adapter = adapter
            binding?.variantContainer?.addView(swatechView.root)
        }
    }

    private fun variantFilter(
        variantPair: List<String>,
        edges: MutableList<Storefront.ProductVariantEdge>,
        variantList: MutableMap<String, String>
    ) {
        val new_pair = StringBuilder()
        for (i in 0 until variantPair.size) {
            if (new_pair.length > 0) {
                new_pair.append(" / ")
            }
            new_pair.append(variantPair.get(i))
        }
        val new_pair_reverse = StringBuilder()
        for (i in variantPair.size - 1 downTo 0) {
            if (new_pair_reverse.length > 0) {
                new_pair_reverse.append(" / ")
            }
            new_pair_reverse.append(variantPair.get(i))
        }
        Log.d(TAG, "variantFilter: " + new_pair)
        Log.d(TAG, "variantFilter: " + new_pair_reverse)
        edges.forEach {
            if (it.node.title.equals(new_pair.toString()) || it.node.title.equals(new_pair_reverse.toString())) {
                variantId = it.node.id
                variantEdge = it.node
                binding?.variantAvailableQty?.visibility = View.VISIBLE
                binding?.variantAvailableQty?.text =
                    it.node.quantityAvailable.toString() + " " + resources.getString(R.string.avaibale_qty_variant)
                setProductPrice(it.node)
                if (it.node.currentlyNotInStock == false) {
                    if (it.node.quantityAvailable == 0) {
                        binding?.addtocart?.text = getString(R.string.out_of_stock)
                        inStock = false
                        adapter.notifyDataSetChanged()
                    } else {
                        binding?.addtocart?.text = getString(R.string.addtocart)
                        inStock = true
                    }
                } else {
                    inStock = true
                    binding?.addtocart?.text = getString(R.string.addtocart)
                }
                Log.d(TAG, "variantFilter: " + variantId)
                variantValidation = variantList
                return
            } else {
                binding?.addtocart?.text = getString(R.string.out_of_stock)
                inStock = false
            }
        }
    }

    private fun setProductPrice(variant: Storefront.ProductVariant?) {
        if (model!!.presentmentCurrency == "nopresentmentcurrency") {
            if (variant?.compareAtPriceV2 != null) {
                val special = java.lang.Double.valueOf(variant.compareAtPriceV2.amount)
                val regular = java.lang.Double.valueOf(variant.priceV2.amount)
                if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                    data!!.regularprice = CurrencyFormatter.setsymbol(
                        variant.compareAtPriceV2.amount,
                        variant.compareAtPriceV2.currencyCode.toString()
                    )
                    data!!.specialprice = CurrencyFormatter.setsymbol(
                        variant.priceV2.amount,
                        variant.priceV2.currencyCode.toString()
                    )
                    data!!.offertext = getDiscount(special, regular).toString() + "%off"
                } else {
                    data!!.regularprice = CurrencyFormatter.setsymbol(
                        variant.priceV2.amount,
                        variant.priceV2.currencyCode.toString()
                    )
                    data!!.specialprice = CurrencyFormatter.setsymbol(
                        variant.compareAtPriceV2.amount,
                        variant.compareAtPriceV2.currencyCode.toString()
                    )
                    data!!.offertext = getDiscount(regular, special).toString() + "%off"
                }
                data!!.isStrike = true
                binding!!.regularprice.setTextColor(resources.getColor(R.color.black))
                binding!!.specialprice.setTextColor(resources.getColor(R.color.black))
                var typeface = Typeface.createFromAsset(assets, "fonts/normal.ttf")
                binding!!.regularprice.typeface = typeface
                binding!!.regularprice.paintFlags =
                    binding!!.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding!!.specialprice.visibility = View.VISIBLE
                binding!!.offertext.visibility = View.VISIBLE
                binding!!.offertext.setTextColor(resources.getColor(R.color.green))
            } else {
                data!!.regularprice = CurrencyFormatter.setsymbol(
                    variant?.priceV2?.amount!!,
                    variant.priceV2?.currencyCode.toString()
                )
                data!!.isStrike = false
                binding!!.specialprice.visibility = View.GONE
                binding!!.offertext.visibility = View.GONE
                binding!!.regularprice.setTextColor(resources.getColor(R.color.black))
                binding!!.regularprice.textSize = 15f
                var typeface = Typeface.createFromAsset(assets, "fonts/bold.ttf")
                binding!!.regularprice.typeface = typeface
                binding!!.regularprice.paintFlags =
                    binding!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        } else {
            val edge = variant?.presentmentPrices?.edges?.get(0)
            if (variant?.compareAtPriceV2 != null) {
                val special = java.lang.Double.valueOf(edge?.node?.compareAtPrice?.amount!!)
                val regular = java.lang.Double.valueOf(edge.node.price.amount)
                if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                    data!!.regularprice = CurrencyFormatter.setsymbol(
                        edge.node.compareAtPrice.amount,
                        edge.node.compareAtPrice.currencyCode.toString()
                    )
                    data!!.specialprice = CurrencyFormatter.setsymbol(
                        edge.node.price.amount,
                        edge.node.price.currencyCode.toString()
                    )
                    data!!.offertext = getDiscount(special, regular).toString() + "%off"
                } else {
                    data!!.regularprice = CurrencyFormatter.setsymbol(
                        edge.node.price.amount,
                        edge.node.price.currencyCode.toString()
                    )
                    data!!.specialprice = CurrencyFormatter.setsymbol(
                        edge.node.compareAtPrice.amount,
                        edge.node.compareAtPrice.currencyCode.toString()
                    )
                    data!!.offertext = getDiscount(regular, special).toString() + "%off"
                }
                data!!.isStrike = true
                binding!!.regularprice.setTextColor(resources.getColor(R.color.black))
                binding!!.specialprice.setTextColor(resources.getColor(R.color.black))
                var typeface = Typeface.createFromAsset(assets, "fonts/normal.ttf")
                binding!!.regularprice.typeface = typeface
                binding!!.regularprice.paintFlags =
                    binding!!.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding!!.specialprice.visibility = View.VISIBLE
                binding!!.offertext.visibility = View.VISIBLE
                binding!!.offertext.setTextColor(resources.getColor(R.color.green))
            } else {
                data!!.regularprice = CurrencyFormatter.setsymbol(
                    edge!!.node.price.amount,
                    edge.node.price.currencyCode.toString()
                )
                data!!.isStrike = false
                binding!!.specialprice.visibility = View.GONE
                binding!!.offertext.visibility = View.GONE
                binding!!.regularprice.setTextColor(resources.getColor(R.color.black))
                binding!!.regularprice.textSize = 15f
                var typeface = Typeface.createFromAsset(assets, "fonts/bold.ttf")
                binding!!.regularprice.typeface = typeface
                binding!!.regularprice.paintFlags =
                    binding!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }

    fun getDiscount(regular: Double, special: Double): Int {
        return ((regular - special) / regular * 100).toInt()
    }

    private fun getEdge(edges: List<Storefront.ProductVariantPricePairEdge>): Storefront.ProductVariantPricePairEdge? {
        var pairEdge: Storefront.ProductVariantPricePairEdge? = null
        try {
            for (i in edges.indices) {
                if (edges[i].node.price.currencyCode.toString() == model!!.presentmentCurrency) {
                    pairEdge = edges[i]
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pairEdge
    }

    inner class ClickHandlers {
        fun addtoCart(view: View, data: ListData) {
            if (inStock) {
                if (variantValidation.values != null) {
                    if (variantValidation.size >= totalVariant!! || singleVariant) {
                        model!!.addToCart(
                            variantId.toString(),
                            binding?.quantity?.text.toString().toInt()
                        )
                        Toast.makeText(
                            view.context,
                            resources.getString(R.string.successcart),
                            Toast.LENGTH_LONG
                        ).show()
                        invalidateOptionsMenu()
                        var cartlistData = JSONObject()
                        cartlistData.put("id", data.product?.id.toString())
                        cartlistData.put("quantity", binding?.quantity?.text.toString())
                        cartlistArray.put(cartlistData.toString())
                        Constant.logAddToCartEvent(
                            cartlistArray.toString(),
                            data.product?.id.toString(),
                            "product",
                            data.product?.variants?.edges?.get(0)?.node?.presentmentPrices?.edges?.get(
                                0
                            )?.node?.price?.currencyCode?.toString(),
                            data.product?.variants?.edges?.get(0)?.node?.presentmentPrices?.edges?.get(
                                0
                            )?.node?.price?.amount?.toDouble()
                                ?: 0.0,
                            this@ProductView
                        )

                        if (featuresModel.firebaseEvents) {
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART) {
                                param(FirebaseAnalytics.Param.ITEM_ID, data.product?.id.toString())
                                param(
                                    FirebaseAnalytics.Param.QUANTITY,
                                    binding?.quantity?.text.toString()
                                )
                            }
                        }
                    } else {
                        Toast.makeText(
                            view.context,
                            resources.getString(R.string.selectvariant),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        view.context,
                        resources.getString(R.string.selectvariant),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    view.context,
                    getString(R.string.outofstock_warning),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun showSizeChart(view: View, data: ListData) {
            val intent = Intent(this@ProductView, Weblink::class.java)
            intent.putExtra("name", resources.getString(R.string.size_chart))
            intent.putExtra("link", sizeChartUrl)
            startActivity(intent)
            Constant.activityTransition(this@ProductView)
        }

        fun addtoWish(view: View, data: ListData) {
            if (inStock) {
                Log.i("MageNative", "In Wish")
                if (model!!.setWishList(data.product?.id.toString())) {
                    Toast.makeText(
                        view.context,
                        resources.getString(R.string.successwish),
                        Toast.LENGTH_LONG
                    ).show()
                    data.addtowish = resources.getString(R.string.alreadyinwish)
                    Glide.with(this@ProductView).load(R.drawable.wishlist_selected)
                        .into(binding?.addtowish!!)

                    var wishlistData = JSONObject()
                    wishlistData.put("id", data.product?.id.toString())
                    wishlistData.put("quantity", 1)
                    whishlistArray.put(wishlistData.toString())
                    Constant.logAddToWishlistEvent(
                        whishlistArray.toString(),
                        data.product?.id.toString(),
                        "product",
                        data.product?.variants?.edges?.get(0)?.node?.presentmentPrices?.edges?.get(0)?.node?.price?.currencyCode?.toString(),
                        data.product?.variants?.edges?.get(0)?.node?.presentmentPrices?.edges?.get(0)?.node?.price?.amount?.toDouble()
                            ?: 0.0,
                        this@ProductView
                    )
                    if (featuresModel.firebaseEvents) {
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST) {
                            param(FirebaseAnalytics.Param.ITEM_ID, data.product?.id.toString())
                            param(FirebaseAnalytics.Param.QUANTITY, 1)
                        }
                    }
                } else {
                    model!!.deleteData(data.product?.id.toString())
                    data.addtowish = resources.getString(R.string.addtowish)
                    Glide.with(this@ProductView).load(R.drawable.wishlist_icon)
                        .into(binding?.addtowish!!)
                }
            } else {
                Toast.makeText(
                    view.context,
                    getString(R.string.outofstock_warning),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun viewAllReview(view: View) {
            var intent = Intent(this@ProductView, AllReviewListActivity::class.java)
            intent.putExtra("reviewList", reviewModel)
            intent.putExtra("product_name", productName)
            intent.putExtra("product_id", getBase64Decode(productID))
            startActivity(intent)
            Constant.activityTransition(view.context)
        }

        fun viewAllJudgeMeReview(view: View) {
            var intent = Intent(this@ProductView, AllJudgeMeReviews::class.java)
            intent.putExtra("reviewList", reviewList)
            intent.putExtra("product_name", productName)
            intent.putExtra("product_id", judgeme_productid)
            startActivity(intent)
            Constant.activityTransition(view.context)
        }

        fun viewAllAliReview(view: View) {
            var intent = Intent(this@ProductView, AllAliReviewsListActivity::class.java)
            intent.putExtra("reviewList", reviewList)
            intent.putExtra("product_name", productName)
            intent.putExtra("product_id", AliProductId)
            intent.putExtra("shop_id", AliShopId)
            startActivity(intent)
            Constant.activityTransition(view.context)
        }

        fun decrease(view: View) {
            if ((binding!!.quantity.text.toString()).toInt() > 1) {
                var quantity: Int = binding!!.quantity.text.toString().toInt()
                quantity--
                binding!!.quantity.text = quantity.toString()
            }
        }

        fun increase(view: View) {
            if (variantValidation.values != null) {
                if (variantValidation.size >= totalVariant!!) {
                    Log.d(TAG, "increase: " + model?.getQtyInCart(variantId.toString()))
                    var total = binding!!.quantity.text.toString().toInt() + model?.getQtyInCart(
                        variantId.toString()
                    )!!
                    if (variantEdge?.currentlyNotInStock == false) {
                        if (total >= binding?.variantAvailableQty?.text.toString().split(" ").get(0)
                                .toInt()
                        ) {
                            Toast.makeText(
                                this@ProductView,
                                getString(R.string.variant_quantity_warning),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            var quantity: Int = binding!!.quantity.text.toString().toInt()
                            quantity++
                            binding!!.quantity.text = quantity.toString()
                        }
                    } else {
                        var quantity: Int = binding!!.quantity.text.toString().toInt()
                        quantity++
                        binding!!.quantity.text = quantity.toString()
                    }
                } else {
                    Toast.makeText(
                        view.context,
                        resources.getString(R.string.selectvariant),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    view.context,
                    resources.getString(R.string.selectvariant),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        fun shareProduct(view: View, data: ListData) {
            val shareString =
                resources.getString(R.string.hey) + "  " + data.product!!.title + "  " + resources.getString(
                    R.string.on
                ) + "  " + resources.getString(R.string.app_name) + "\n" + data.product!!.onlineStoreUrl + "?pid=" + data.product!!.id.toString()
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                view.context.resources.getString(R.string.app_name)
            )
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareString)
            view.context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    view.context.resources.getString(R.string.share)
                )
            )
            Constant.activityTransition(view.context)
        }


        fun showAR(view: View, data: ListData) {
            try {
                Log.d(TAG, "showAR: " + mediaList)
                var dialog = Dialog(this@ProductView, R.style.WideDialog)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                var dialogBinding = DataBindingUtil.inflate<ArimagesDialogBinding>(
                    layoutInflater,
                    R.layout.arimages_dialog,
                    null,
                    false
                )
                dialog.setContentView(dialogBinding.root)
                dialogBinding.closeBut.setOnClickListener {
                    dialog.dismiss()
                }
                model?.filterArModel(mediaList)?.observe(this@ProductView, Observer {
                    arImagesAdapter.setData(it)
                    dialogBinding.arList.adapter = arImagesAdapter
                    if (it.size == 1) {
                        try {
                            val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
                            val intentUri: Uri =
                                Uri.parse("https://arvr.google.com/scene-viewer/1.1").buildUpon()
                                    .appendQueryParameter("file", data.arimage)
                                    .build()
                            sceneViewerIntent.data = intentUri
                            sceneViewerIntent.setPackage("com.google.ar.core")
                            startActivity(sceneViewerIntent)
                            Constant.activityTransition(view.context)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@ProductView,
                                getString(R.string.ar_error_text),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        dialog.show()
                    }
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun rateProduct(view: View, data: ListData) {
            var bottomsheet = Dialog(this@ProductView, R.style.WideDialog)
            bottomsheet.window?.setBackgroundDrawableResource(android.R.color.transparent)
            bottomsheet.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            var reviewFormBinding = DataBindingUtil.inflate<ReviewFormBinding>(
                layoutInflater,
                R.layout.review_form,
                null,
                false
            )
            bottomsheet.setContentView(reviewFormBinding.root)
            reviewFormBinding.ratingBar.progressTintList =
                ColorStateList.valueOf(Color.parseColor(themeColor))
            bottomsheet.setCancelable(false)
            reviewFormBinding.closeBut.setOnClickListener {
                bottomsheet.dismiss()
            }
            reviewFormBinding.submitReview.setOnClickListener {
                if (TextUtils.isEmpty(reviewFormBinding.nameEdt.text.toString().trim())) {
                    reviewFormBinding.nameEdt.error = getString(R.string.name_validation)
                    reviewFormBinding.nameEdt.requestFocus()
                } else if (TextUtils.isEmpty(reviewFormBinding.titleEdt.text.toString().trim())) {
                    reviewFormBinding.titleEdt.error = getString(R.string.review_title_validation)
                    reviewFormBinding.titleEdt.requestFocus()
                } else if (TextUtils.isEmpty(reviewFormBinding.bodyEdt.text.toString().trim())) {
                    reviewFormBinding.bodyEdt.error = getString(R.string.review_validation)
                    reviewFormBinding.bodyEdt.requestFocus()
                } else if (TextUtils.isEmpty(reviewFormBinding.emailEdt.text.toString().trim())) {
                    reviewFormBinding.emailEdt.error = getString(R.string.email_validation)
                    reviewFormBinding.emailEdt.requestFocus()
                } else if (!model?.isValidEmail(
                        reviewFormBinding.emailEdt.text.toString().trim()
                    )!!
                ) {
                    reviewFormBinding.emailEdt.error = resources.getString(R.string.invalidemail)
                    reviewFormBinding.emailEdt.requestFocus()
                } else {
                    model?.getcreateReview(
                        Urls(application as MyApplication).mid,
                        reviewFormBinding.ratingBar.rating.toString(),
                        getBase64Decode(productID)!!,
                        reviewFormBinding.nameEdt.text.toString().trim(),
                        reviewFormBinding.emailEdt.text.toString().trim(),
                        reviewFormBinding.titleEdt.text.toString().trim(),
                        reviewFormBinding.bodyEdt.text.toString().trim()
                    )
                    bottomsheet.dismiss()
                }
            }
            bottomsheet.show()
        }

        fun rateProductJudgeMe(view: View, data: ListData) {
            var intent = Intent(this@ProductView, JudgeMeCreateReview::class.java)
            intent.putExtra("external_id", external_id)
            startActivityForResult(intent, 105)
            Constant.activityTransition(view.context)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 105) {
            if (featuresModel.judgemeProductReview) {
                if (featuresModel.judgemeProductReview) {
                    model?.judgemeProductID(
                        Urls.JUDGEME_GETPRODUCTID + product_handle,
                        product_handle!!,
                        Urls.JUDGEME_APITOKEN,
                        Urls(application as MyApplication).shopdomain
                    )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.m_product, menu)
        val item = menu.findItem(R.id.cart_item)
        item.setActionView(R.layout.m_count)
        val notifCount = item.actionView
        val textView = notifCount.findViewById<TextView>(R.id.count)
        textView.text = "" + cartCount
        notifCount.setOnClickListener {
            val mycartlist = Intent(this@ProductView, CartList::class.java)
            startActivity(mycartlist)
            Constant.activityTransition(this)
        }
        return true
    }

    private fun submityptporeview() {
        model?.NResponse(
            "VuCs0uv4gPpRuMAMYS0msr1XozTDZunonCRRh6fC",
            productsku.toString(),
            productName.toString(),
            data?.product!!.onlineStoreUrl,
            MagePrefs.getCustomerFirstName().toString(),
            MagePrefs.getCustomerEmail().toString(),
            yotpo_reviewbody.text.toString(),
            yotpo_reviewtitle.text.toString(),
            yotpo_rating_bar.rating.toString()
        )?.observe(this, Observer { this.showData(it) })
    }

    private fun showData(response: ApiResponse?) {
        Log.i("RESPONSEGET", "" + response?.data)
        receiveReview(response?.data)
    }

    private fun receiveReview(data: JsonElement?) {
        val jsondata = JSONObject(data.toString())
        Log.i("messagereview", "" + jsondata)
        try {
            if (jsondata.getString("message").equals("ok")) {
                val alertDialog: AlertDialog = AlertDialog.Builder(this@ProductView).create()
                alertDialog.setTitle("Thank You")
                alertDialog.setMessage("Your review has been submitted successfully.")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                alertDialog.show()
            }
            Handler().postDelayed({ finish() }, 2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }
}
