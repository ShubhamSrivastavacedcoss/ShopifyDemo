package com.shopifydemodemo.app.productsection.activities

import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.databinding.ActivityAllJudgeMeReviewsBinding
import com.shopifydemodemo.app.productsection.adapters.AllReviewListAdapter
import com.shopifydemodemo.app.productsection.models.Review
import com.shopifydemodemo.app.productsection.viewmodels.ProductViewModel
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.Urls
import com.shopifydemodemo.app.utils.ViewModelFactory
import org.json.JSONObject
import javax.inject.Inject

class AllJudgeMeReviews : NewBaseActivity() {
    private var binding: ActivityAllJudgeMeReviewsBinding? = null

    @Inject
    lateinit var reviewAdapter: AllReviewListAdapter

    @Inject
    lateinit var factory: ViewModelFactory
    private var model: ProductViewModel? = null
    private var reviewList: ArrayList<Review>? = null
    private var productName: String? = null
    private var productID: String? = null
    private var page: Int = 1
    private var isLoading: Boolean = true

    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = recyclerView.layoutManager!!.childCount
            val totalItemCount = recyclerView.layoutManager!!.itemCount
            var firstVisibleItemPosition = 0
            if (recyclerView.layoutManager is LinearLayoutManager) {
                firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            } else if (recyclerView.layoutManager is GridLayoutManager) {
                firstVisibleItemPosition = (recyclerView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
            }
            if (!recyclerView.canScrollVertically(1)) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0
                        && totalItemCount >= reviewList!!.size && isLoading) {
                    page++
                    model?.judgemeReviewIndex(productID!!, Urls.JUDGEME_APITOKEN, Urls(application as MyApplication).shopdomain, 5, page)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_all_judge_me_reviews, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doAllJudgeMeReviewListInjection(this)
        model = ViewModelProvider(this, factory).get(ProductViewModel::class.java)
        model?.context = this
        showBackButton()
        model?.getjudgeMeReviewIndex?.observe(this, Observer { this.consumeJudgeMeReview(it) })
        if (intent.hasExtra("reviewList") && intent.hasExtra("product_name") && intent.hasExtra("product_id")) {
            reviewList = intent.getSerializableExtra("reviewList") as ArrayList<Review>
            productName = intent.getStringExtra("product_name")
            productID = intent.getStringExtra("product_id")
            reviewAdapter.setData(reviewList)
            binding?.reviewList?.adapter = reviewAdapter
            showTittle(intent.getStringExtra("product_name")?:"")
        }
        binding?.reviewList?.addOnScrollListener(recyclerViewOnScrollListener)
    }

    private fun consumeJudgeMeReview(response: ApiResponse?) {
        var responseData = JSONObject(response?.data.toString())
        var reviews = responseData.getJSONArray("reviews")
        var review_model: Review? = null
        reviewList = ArrayList<Review>()
        if (reviews.length() > 0) {
            for (i in 0 until reviews.length()) {
                review_model = Review(reviews.getJSONObject(i).getString("body"),
                        reviews.getJSONObject(i).getString("id"),
                        reviews.getJSONObject(i).getString("rating"),
                        reviews.getJSONObject(i).getString("rating"),
                        reviews.getJSONObject(i).getString("created_at"),
                        reviews.getJSONObject(i).getJSONObject("reviewer").getString("name"),
                        reviews.getJSONObject(i).getString("title")
                )
                reviewList?.add(review_model)
            }
            reviewAdapter?.reviwList?.addAll(reviewList!!)
            reviewAdapter?.notifyDataSetChanged()
        } else {
            isLoading = false
        }
    }
}