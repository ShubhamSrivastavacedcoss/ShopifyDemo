package com.shopifydemodemo.app.quickadd_section.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.databinding.ActivityQuickAddBinding
import com.shopifydemodemo.app.dbconnection.entities.CartItemData
import com.shopifydemodemo.app.network_transaction.CustomResponse
import com.shopifydemodemo.app.network_transaction.doGraphQLQueryGraph
import com.shopifydemodemo.app.quickadd_section.adapter.QuickVariantAdapter
import com.shopifydemodemo.app.quickadd_section.adapter.QuickVariantAdapter.Companion.selectedPosition
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.shopifyqueries.Query
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.GraphQLResponse
import com.shopifydemodemo.app.utils.Status
import com.shopifydemodemo.app.wishlistsection.activities.WishList
import com.shopifydemodemo.app.wishlistsection.viewmodels.WishListViewModel
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Runnable

class QuickAddActivity(context: Context, var activity: Context? = null, theme: Int, var product_id: String, var repository: Repository, var wishListViewModel: WishListViewModel? = null, var position: Int? = null, var wishlistData: MutableList<Storefront.Product>? = null) : BottomSheetDialog(context, theme) {
    var binding: ActivityQuickAddBinding? = null
    private val TAG = "QuickAddActivity"
    lateinit var app: MyApplication
    private var inStock: Boolean = true
    var variant_id: String? = null
    var carttArray = JSONArray()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var product_price: Double = 0.0
    private var variant: Storefront.ProductVariantEdge? = null
    var bottomSheetDialog: BottomSheetDialog? = null
    var presentment_currency = "nopresentmentcurrency"
    var currency_list: ArrayList<Storefront.CurrencyCode>? = null
    var quantity: Int = 1
    lateinit var quickVariantAdapter: QuickVariantAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_quick_add, null, false)
        setContentView(binding?.root!!)
        this.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialog = this
        firebaseAnalytics = Firebase.analytics
        //   binding?.availableQty?.textSize = 12f
        initView()

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

    fun initView() {
        currency_list = ArrayList<Storefront.CurrencyCode>()
        val currency = runBlocking(Dispatchers.IO) {
            if (repository.localData[0].currencycode != null) {
                return@runBlocking repository.localData[0].currencycode!!
            } else {
                return@runBlocking "nopresentmentcurrency"
            }
        }

        currency_list?.add(Storefront.CurrencyCode.valueOf(currency))

        binding?.availableQty?.textSize = 14f
        quickVariantAdapter = QuickVariantAdapter()
        Log.d(TAG, "initView: " + currency_list)
        doGraphQLQueryGraph(repository, Query.getProductById(product_id!!, currency_list!!), customResponse = object : CustomResponse {
            override fun onSuccessQuery(result: GraphCallResult<Storefront.QueryRoot>) {
                invoke(result)
            }
        }, context = context)
        setPresentmentCurrencyForModel()
        binding?.handler = VariantClickHandler()
    }

    private fun setPresentmentCurrencyForModel() {
        try {
            val runnable = Runnable {
                if (repository.localData[0].currencycode == null) {
                    quickVariantAdapter.presentmentcurrency = "nopresentmentcurrency"
                } else {
                    quickVariantAdapter.presentmentcurrency = repository.localData[0].currencycode
                }
            }
            Thread(runnable).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun invoke(result: GraphCallResult<Storefront.QueryRoot>) {
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
                    Toast.makeText(context, "" + errormessage, Toast.LENGTH_SHORT).show()
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(500)
                        this@QuickAddActivity.show()
                    }

                    var productedge: Storefront.Product? = null
                    productedge = result.data!!.node as Storefront.Product
                    // a.previewImage

                    Log.i("MageNative", "Product_id" + productedge!!.id.toString())
                    setProductData(productedge)
                }
            }
            Status.ERROR -> Toast.makeText(context, reponse.error!!.error.message, Toast.LENGTH_SHORT).show()
            else -> {
            }
        }
    }

    private fun setProductData(productedge: Storefront.Product) {
        quickVariantAdapter.setData(productedge.variants.edges, context, itemClick = object : QuickVariantAdapter.ItemClick {
            override fun variantSelection(variantData: Storefront.ProductVariantEdge) {
                variant_id = variantData.node.id.toString()
                variant = variantData
                quantity = 1
                binding?.quantity?.text = "1"
                binding?.availableQty?.visibility = View.VISIBLE
                binding?.availableQty?.text = variantData.node.quantityAvailable.toString() + " " + context.resources.getString(R.string.avaibale_qty_variant)
                if (variantData.node.currentlyNotInStock == true) {
                    binding?.addCart?.text = context.getString(R.string.addtocart)
                    inStock = true
                } else {
                    if (variantData.node.quantityAvailable == 0) {
                        binding?.addCart?.text = context.getString(R.string.out_of_stock)
                        inStock = false
                    } else {
                        binding?.addCart?.text = context.getString(R.string.addtocart)
                        inStock = true
                    }
                }
                Log.d(TAG, "variantSelection: " + variantData.node.id)

            }
        })
        var cartlistData = JSONObject()
        cartlistData.put("id", productedge.id)
        cartlistData.put("quantity", 1)
        carttArray.put(cartlistData.toString())
        product_price = productedge?.variants?.edges?.get(0)?.node?.presentmentPrices?.edges?.get(0)?.node?.price?.amount?.toDouble()
                ?: 0.0
        binding?.variantList?.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        selectedPosition = -1
        binding?.variantList?.adapter = quickVariantAdapter
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
                    val qt = data.qty
                    data.qty = qt + quantity
                    repository.updateSingLeItem(data)
                }
                Log.i("MageNative", "CartCount : " + repository.allCartItems.size)
            }
            Thread(runnable).start()
            GlobalScope.launch(Dispatchers.Main) {
                if (activity is NewBaseActivity) {
                    Toast.makeText(activity, activity?.resources?.getString(R.string.successcart), Toast.LENGTH_LONG).show()
                    (activity as NewBaseActivity).invalidateOptionsMenu()
                }
            }
            Constant.logAddToCartEvent(carttArray.toString(), product_id, "product", presentment_currency, product_price, activity
                    ?: Activity())
            if (SplashViewModel.featuresModel.firebaseEvents) {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART) {
                    param(FirebaseAnalytics.Param.ITEM_ID, product_id)
                    param(FirebaseAnalytics.Param.QUANTITY, quantity.toString())
                }
            }
            if (wishListViewModel != null) {
                if (activity is WishList) {
                    wishListViewModel!!.deleteData(product_id)
                    wishlistData!!.removeAt(position!!)
                    (activity as WishList).adapter.notifyItemRemoved(position!!)
                    (activity as WishList).adapter.notifyItemRangeChanged(position!!, wishlistData!!.size)
                    wishListViewModel!!.update(true)
                    (activity as WishList).invalidateOptionsMenu()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class VariantClickHandler {

        fun addcart(view: View) {
            if (inStock) {
                if (variant_id == null) {
                    Toast.makeText(view.context, view.context.resources.getString(R.string.selectvariant), Toast.LENGTH_LONG).show()
                } else {
                    addToCart(variant_id!!, quantity)
                    Toast.makeText(context, context.getString(R.string.successcart), Toast.LENGTH_LONG).show()
                    bottomSheetDialog?.dismiss()
                }
            } else {
                Toast.makeText(view.context, view.context.getString(R.string.outofstock_warning), Toast.LENGTH_SHORT).show()
            }
        }

        fun closeDialog(view: View) {
            bottomSheetDialog?.dismiss()
        }

        fun decrease(view: View) {
            if ((binding?.quantity?.text?.toString())?.toInt() ?: 0 > 1) {
                quantity--
                binding?.quantity?.text = quantity.toString()
            }
        }

        fun increase(view: View) {
            if (variant_id == null) {
                Toast.makeText(view.context, view.context.resources.getString(R.string.selectvariant), Toast.LENGTH_LONG).show()
            } else {
                if (variant?.node?.currentlyNotInStock == true) {
                    quantity++
                    binding?.quantity?.text = quantity.toString()
                } else {
                    var total = binding!!.quantity.text.toString().toInt() + getQtyInCart(variant_id!!)
                    if (total >= binding?.availableQty?.text.toString().split(" ").get(0).toInt()) {
                        Toast.makeText(view.context, view.context.getString(R.string.variant_quantity_warning), Toast.LENGTH_LONG).show()
                    } else {
                        quantity++
                        binding?.quantity?.text = quantity.toString()
                    }
                }
            }
        }
    }
}