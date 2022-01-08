package com.shopifydemodemo.app.productsection.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MProductlistitemBinding
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.cartsection.activities.CartList
import com.shopifydemodemo.app.databinding.SortDialogLayoutBinding
import com.shopifydemodemo.app.productsection.adapters.ProductFilterRecylerAdapter
import com.shopifydemodemo.app.productsection.adapters.ProductRecyclerListAdapter
import com.shopifydemodemo.app.productsection.adapters.ProductRecylerGridAdapter
import com.shopifydemodemo.app.productsection.viewmodels.ProductListModel
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.ViewModelFactory
import kotlinx.android.synthetic.main.m_productmain.view.*
import org.json.JSONArray
import org.json.JSONObject

import javax.inject.Inject

class ProductList : NewBaseActivity() {
    private var binding: MProductlistitemBinding? = null
    private var productlist: RecyclerView? = null

    @Inject
    lateinit var factory: ViewModelFactory
    var productListModel: ProductListModel? = null
    private var products: MutableList<Storefront.ProductEdge>? = null
    private var productcursor: String? = null
    private var listEnabled: Boolean = false

    @Inject
    lateinit var product_grid_adapter: ProductRecylerGridAdapter

    @Inject
    lateinit var product_list_adapter: ProductRecyclerListAdapter
    private var flag = true
    private var filter_by: TextView? = null
    private var collection_response: JSONObject? = null
    private var handle: String? = null
    var tag_list = ArrayList<String>()

    @Inject
    lateinit var product_filter_adapter: ProductFilterRecylerAdapter
    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (tags.isEmpty()) {
                val visibleItemCount = recyclerView.layoutManager!!.childCount
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                var firstVisibleItemPosition = 0
                if (recyclerView.layoutManager is LinearLayoutManager) {
                    firstVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                } else if (recyclerView.layoutManager is GridLayoutManager) {
                    firstVisibleItemPosition =
                        (recyclerView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
                }
                if (!recyclerView.canScrollVertically(1)) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition > 0
                        && totalItemCount >= products!!.size
                    ) {
                        productListModel!!.number = 20
                        productListModel!!.cursor = productcursor!!
                    }
                }
            }
        }
    }
    private var tags: String = ""
    private var productsJsonarr: JSONArray? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_productlistitem, group, true)
        filter_by = binding!!.root.findViewById(R.id.filter_but)
        productlist = setLayout(binding!!.root.findViewById(R.id.productlist), "grid")
        showBackButton()
        if (intent.hasExtra("tittle") && intent.getStringExtra("tittle") != null) {
            showTittle(intent.getStringExtra("tittle") ?: "")
        }
        (application as MyApplication).mageNativeAppComponent!!.doProductListInjection(this)
        productListModel = ViewModelProvider(this, factory).get(ProductListModel::class.java)
        productListModel!!.context = this
        productListModel?.collectionData?.observe(this, Observer { this.collectionResponse(it) })
        if (intent.getStringExtra("ID") != null) {
            productListModel!!.setcategoryID(intent.getStringExtra("ID")!!)
        }
        if (intent.getStringExtra("handle") != null) {
            productListModel!!.setcategoryHandle(intent.getStringExtra("handle")!!)
        }
        if (intent.getStringExtra("ID") == null && intent.getStringExtra("handle") == null) {
            productListModel!!.shopID = "allproduct"
            flag = false
        }
        productListModel!!.message.observe(this, Observer { this.showToast(it) })
        productListModel!!.Response()
        productListModel!!.filteredproducts.observe(
            this,
            Observer<MutableList<Storefront.ProductEdge>> { this.setRecylerData(it) })
        productlist!!.addOnScrollListener(recyclerViewOnScrollListener)
        binding?.mainview?.sort_but?.setOnClickListener {
            openSortDialog()
        }
        binding?.mainview?.grid_but?.setOnClickListener {
            listEnabled = false
            products = null
            productListModel!!.cursor = "nocursor"
            binding?.mainview?.productListContainer?.visibility = View.GONE
            productlist = setLayout(binding!!.root.findViewById(R.id.productlist), "grid")
            binding?.mainview?.grid_but?.setImageResource(R.drawable.grid_icon_selected)
            binding?.mainview?.list_but?.setImageResource(R.drawable.list_icon)
        }
        binding?.mainview?.list_but?.setOnClickListener {
            productlist?.layoutManager = LinearLayoutManager(this)
            listEnabled = true
            products = null
            binding?.mainview?.productListContainer?.visibility = View.GONE
            productListModel!!.cursor = "nocursor"
            binding?.mainview?.grid_but?.setImageResource(R.drawable.grid_icon)
            binding?.mainview?.list_but?.setImageResource(R.drawable.list_icon_selected)
        }
        filter_by?.setOnClickListener {
            productListModel?.collectionTags?.observe(this, Observer { this.consumeTags(it) })
        }
        if (SplashViewModel.featuresModel.productListEnabled) {
            binding?.mainview?.grid_but?.visibility = View.VISIBLE
            binding?.mainview?.list_but?.visibility = View.VISIBLE
        } else {
            binding?.mainview?.grid_but?.visibility = View.GONE
            binding?.mainview?.list_but?.visibility = View.GONE
        }
        if (SplashViewModel.featuresModel.filterEnable) {
            filter_by?.visibility = View.VISIBLE
        }
    }

    private fun consumeTags(response: ApiResponse?) {
        if (response?.data != null) {
            var responseData = JSONObject(response.data.toString())
            if (responseData.getBoolean("success")) {
                collection_response = responseData.getJSONObject("data")
                productListModel?.collectionData?.observe(
                    this,
                    Observer { this.collectionResponses(it) })
            }
        }
    }

    private fun collectionResponses(it: Storefront.Collection?) {
        if (it?.title != null) {
            showTittle(it.title)
            handle = it.handle
            productListModel?.setcategoryHandle(it.handle)
            productListModel?.categoryHandle = it.handle
            var tags = collection_response?.names()
            tag_list = ArrayList<String>()
            var tags_array = collection_response?.getJSONArray(it.handle)
            for (j in 0 until tags_array?.length()!!) {
                if (tags_array.get(j).toString().contains("_")) {
                    tag_list.add(tags_array.get(j).toString())
                }
            }
            Log.i("collresponse", "" + tag_list)
            Log.i("collresponse", "" + it.handle)
            var intent = Intent(this, FilterActivity::class.java)
            intent.putStringArrayListExtra("filterData", tag_list)
            intent.putExtra("handle", handle)
            startActivityForResult(intent, 200)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            this.products = null
            tags = data?.getStringExtra("result").toString()
            if (tags.isEmpty()) {
                productsJsonarr = null
                productListModel!!.filteredproducts.observe(
                    this,
                    Observer<MutableList<Storefront.ProductEdge>> { this.setRecylerData(it) })
            } else {
                productListModel!!.tags_ = tags
                productListModel!!.ResponseApiFilterProducts().observe(this,
                    Observer<ApiResponse> { this.consumeProductsResponse(it) })
                FilterActivity.listMap = HashMap<String, String>()
            }
        }
    }

    fun consumeProductsResponse(Response: ApiResponse) {
        var response = JSONObject(Response.data.toString())
        Log.i("inside", "" + response)
        var items = response.getJSONArray("data")
        productsJsonarr = null
        if (items.length() > 0) {
            if (this.productsJsonarr == null) {
                this.productsJsonarr = items
                product_filter_adapter.setData(
                    this.productsJsonarr,
                    this@ProductList,
                    productListModel!!.repository,
                    productListModel!!.presentmentCurrency
                )
                productlist!!.adapter = product_filter_adapter
                product_filter_adapter.notifyDataSetChanged()
            } else {
                for (i in 0 until items.length()) {
                    this.productsJsonarr!!.put(items.getJSONObject(i))
                }
                product_filter_adapter.notifyDataSetChanged()
            }
            //totalproductsCount = productsJsonarr!!.length()
            //productapicursor = (this.productsJsonarr!!.length() + 1).toString()
            product_filter_adapter.notifyDataSetChanged()

            Log.i("insideres", "items " + items)
        } else {
            showToast(resources.getString(R.string.noproducts))
            //product_list_adapter.products.clear()
            product_list_adapter.notifyDataSetChanged()
        }
    }

    private fun openSortDialog() {
        var dialog = BottomSheetDialog(this, R.style.WideDialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        var sortDialogLayoutBinding = DataBindingUtil.inflate<SortDialogLayoutBinding>(
            layoutInflater,
            R.layout.sort_dialog_layout,
            null,
            false
        )
        dialog.setContentView(sortDialogLayoutBinding.root)
        sortDialogLayoutBinding.atoz.setOnClickListener {
            if (flag) {
                productListModel!!.sortKeys = Storefront.ProductCollectionSortKeys.TITLE
            } else {
                productListModel!!.keys = Storefront.ProductSortKeys.TITLE
            }
            productListModel!!.isDirection = false
            products = null
            productListModel!!.number = 10
            productListModel!!.cursor = "nocursor"
            dialog.dismiss()
        }
        sortDialogLayoutBinding.ztoa.setOnClickListener {
            if (flag) {
                productListModel!!.sortKeys = Storefront.ProductCollectionSortKeys.TITLE
            } else {
                productListModel!!.keys = Storefront.ProductSortKeys.TITLE
            }
            productListModel!!.isDirection = true
            products = null
            productListModel!!.number = 10
            productListModel!!.cursor = "nocursor"
            dialog.dismiss()
        }
        sortDialogLayoutBinding.htol.setOnClickListener {
            if (flag) {
                productListModel!!.sortKeys = Storefront.ProductCollectionSortKeys.PRICE
            } else {
                productListModel!!.keys = Storefront.ProductSortKeys.PRICE
            }
            productListModel!!.isDirection = true
            products = null
            productListModel!!.number = 10
            productListModel!!.cursor = "nocursor"
            dialog.dismiss()
        }
        sortDialogLayoutBinding.ltoh.setOnClickListener {
            if (flag) {
                productListModel!!.sortKeys = Storefront.ProductCollectionSortKeys.PRICE
            } else {
                productListModel!!.keys = Storefront.ProductSortKeys.PRICE
            }
            productListModel!!.isDirection = false
            products = null
            productListModel!!.number = 10
            productListModel!!.cursor = "nocursor"
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun collectionResponse(it: Storefront.Collection?) {
        if (it?.title != null) {
            showTittle(it.title)
        }
    }

    override fun onResume() {
        super.onResume()
        if (textView != null) {
            textView!!.text = "" + productListModel!!.cartCount
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.m_product, menu)
        val item = menu.findItem(R.id.cart_item)
        item.setActionView(R.layout.m_count)
        val notifCount = item.actionView
        textView = notifCount.findViewById<TextView>(R.id.count)
        textView?.text = "" + cartCount
        notifCount.setOnClickListener {
            val mycartlist = Intent(this, CartList::class.java)
            startActivity(mycartlist)
            Constant.activityTransition(this)
        }
        return true
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun setRecylerData(products: MutableList<Storefront.ProductEdge>) {
        try {
            if (products.size > 0) {
                binding?.mainview?.productListContainer?.visibility = View.VISIBLE
                if (!listEnabled) {
                    product_grid_adapter.presentmentcurrency =
                        productListModel!!.presentmentCurrency
                    if (this.products == null) {
                        this.products = products
                        product_grid_adapter.setData(
                            this.products,
                            this@ProductList,
                            productListModel!!.repository
                        )
                        productlist!!.adapter = product_grid_adapter
                    } else {
                        this.products!!.addAll(products)
                        product_grid_adapter.notifyDataSetChanged()
                    }
                    productcursor = this.products!![this.products!!.size - 1].cursor
                    Log.i("MageNative", "Cursor : " + productcursor!!)
                } else {
                    product_list_adapter.presentmentcurrency =
                        productListModel!!.presentmentCurrency
                    if (this.products == null) {
                        this.products = products
                        product_list_adapter.setData(
                            this.products,
                            this@ProductList,
                            productListModel!!.repository
                        )
                        productlist!!.adapter = product_list_adapter
                    } else {
                        this.products!!.addAll(products)
                        product_list_adapter.notifyDataSetChanged()
                    }
                    productcursor = this.products!![this.products!!.size - 1].cursor
                    Log.i("MageNative", "Cursor : " + productcursor!!)
                }
            } else {
                showToast(resources.getString(R.string.noproducts))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
