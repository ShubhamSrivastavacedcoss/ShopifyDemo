package com.shopifydemodemo.app.productsection.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.basesection.models.ListData
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.databinding.MProductitemBinding
import com.shopifydemodemo.app.productsection.activities.ProductList
import com.shopifydemodemo.app.productsection.activities.ProductView
import com.shopifydemodemo.app.productsection.viewholders.ProductItem
import com.shopifydemodemo.app.quickadd_section.activities.QuickAddActivity
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.CurrencyFormatter
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import javax.inject.Inject


class ProductRecylerGridAdapter @Inject
constructor() : RecyclerView.Adapter<ProductItem>() {
    private var layoutInflater: LayoutInflater? = null
    lateinit var products: MutableList<Storefront.ProductEdge>
    private var activity: Activity? = null
    private var repository: Repository? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var presentmentcurrency: String? = null
    var whilistArray = JSONArray()
    fun setData(products: List<Storefront.ProductEdge>?, activity: Activity, repository: Repository) {
        this.products = products as MutableList<Storefront.ProductEdge>
        this.activity = activity
        this.repository = repository
        firebaseAnalytics = Firebase.analytics
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int {
        var viewtype = 0
        if (!products[position].node.availableForSale) {
            viewtype = -1
        }
        return viewtype
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItem {
        val binding = DataBindingUtil.inflate<MProductitemBinding>(LayoutInflater.from(parent.context), R.layout.m_productitem, parent, false)
        return ProductItem(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ProductItem, position: Int) {
        val variant = this.products[position].node.variants.edges[0].node
        val data = ListData()
        Log.i("MageNative", "Product ID" + this.products[position].node.id)
        data.product = this.products[position].node
        data.textdata = this.products[position].node.title
        data.description = this.products[position].node.description
        if (presentmentcurrency == "nopresentmentcurrency") {
            data.regularprice = CurrencyFormatter.setsymbol(variant.priceV2.amount, variant.priceV2.currencyCode.toString())
            if (variant.compareAtPriceV2 != null) {
                val special = java.lang.Double.valueOf(variant.compareAtPriceV2.amount)
                val regular = java.lang.Double.valueOf(variant.priceV2.amount)
                if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                    data.regularprice = CurrencyFormatter.setsymbol(variant.compareAtPriceV2.amount, variant.compareAtPriceV2.currencyCode.toString())
                    data.specialprice = CurrencyFormatter.setsymbol(variant.priceV2.amount, variant.priceV2.currencyCode.toString())
                    data.offertext = getDiscount(special, regular).toString() + "%off"
                } else {
                    data.regularprice = CurrencyFormatter.setsymbol(variant.priceV2.amount, variant.priceV2.currencyCode.toString())
                    data.specialprice = CurrencyFormatter.setsymbol(variant.compareAtPriceV2.amount, variant.compareAtPriceV2.currencyCode.toString())
                    data.offertext = getDiscount(regular, special).toString() + "%off"
                }
                holder.binding!!.regularprice.setTextColor(activity?.resources?.getColor(R.color.black)!!)
                holder.binding!!.specialprice.setTextColor(activity?.resources?.getColor(R.color.black)!!)
                var typeface = Typeface.createFromAsset(activity?.assets, "fonts/normal.ttf")
                holder.binding!!.regularprice.setTypeface(typeface)
                holder.binding!!.regularprice.paintFlags = holder.binding!!.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding!!.specialprice.visibility = View.VISIBLE
                holder.binding!!.offertext.visibility = View.VISIBLE
                holder.binding!!.offertext.setTextColor(activity!!.resources.getColor(R.color.green))
            } else {
                holder.binding!!.specialprice.visibility = View.GONE
                holder.binding!!.offertext.visibility = View.GONE
                holder.binding!!.regularprice.setTextColor(activity?.resources?.getColor(R.color.black)!!)
                holder.binding!!.regularprice.textSize = 15f
                var typeface = Typeface.createFromAsset(activity?.assets, "fonts/bold.ttf")
                holder.binding!!.regularprice.setTypeface(typeface)
                holder.binding!!.regularprice.paintFlags = holder.binding!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        } else {
            val edge = variant.presentmentPrices.edges[0]
            data.regularprice = CurrencyFormatter.setsymbol(edge!!.node.price.amount, edge.node.price.currencyCode.toString())
            if (variant.compareAtPriceV2 != null) {
                val special = java.lang.Double.valueOf(edge.node.compareAtPrice.amount)
                val regular = java.lang.Double.valueOf(edge.node.price.amount)
                if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                    data.regularprice = CurrencyFormatter.setsymbol(edge.node.compareAtPrice.amount, edge.node.compareAtPrice.currencyCode.toString())
                    data.specialprice = CurrencyFormatter.setsymbol(edge.node.price.amount, edge.node.price.currencyCode.toString())
                    data.offertext = getDiscount(special, regular).toString() + "%off"
                } else {
                    data.regularprice = CurrencyFormatter.setsymbol(edge.node.price.amount, edge.node.price.currencyCode.toString())
                    data.specialprice = CurrencyFormatter.setsymbol(edge.node.compareAtPrice.amount, edge.node.compareAtPrice.currencyCode.toString())
                    data.offertext = getDiscount(regular, special).toString() + "%off"
                }
                holder.binding!!.regularprice.setTextColor(activity?.resources?.getColor(R.color.black)!!)
                holder.binding!!.specialprice.setTextColor(activity?.resources?.getColor(R.color.black)!!)
                var typeface = Typeface.createFromAsset(activity?.assets, "fonts/normal.ttf")
                holder.binding!!.regularprice.setTypeface(typeface)
                holder.binding!!.regularprice.paintFlags = holder.binding!!.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding!!.specialprice.visibility = View.VISIBLE
                holder.binding!!.offertext.visibility = View.VISIBLE
                holder.binding!!.offertext.setTextColor(activity!!.resources.getColor(R.color.green))
            } else {
                holder.binding!!.specialprice.visibility = View.GONE
                holder.binding!!.offertext.visibility = View.GONE
                holder.binding!!.regularprice.setTextColor(activity?.resources?.getColor(R.color.black)!!)
                holder.binding!!.regularprice.textSize = 15f
                var typeface = Typeface.createFromAsset(activity?.assets, "fonts/bold.ttf")
                holder.binding!!.regularprice.setTypeface(typeface)
                holder.binding!!.regularprice.paintFlags = holder.binding!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
        if (SplashViewModel.featuresModel.in_app_wishlist) {
            if ((activity as ProductList).productListModel?.isInwishList(data.product?.id.toString())!!) {
                data!!.addtowish = activity?.resources?.getString(R.string.alreadyinwish)
                Glide.with(activity!!)
                        .load(R.drawable.wishlist_selected)
                        .into(holder?.binding?.wishlistBut!!)
            } else {
                data!!.addtowish = activity?.resources?.getString(R.string.addtowish)
                Glide.with(activity!!)
                        .load(R.drawable.wishlist_icon)
                        .into(holder?.binding?.wishlistBut!!)
            }
        }

        if (SplashViewModel.featuresModel.outOfStock!!) {
            if (!this.products[position].node.availableForSale) {
                holder?.binding?.outOfStock?.visibility = View.VISIBLE
            } else {
                holder?.binding?.outOfStock?.visibility = View.GONE
            }
        }
        holder.binding!!.listdata = data
        val model = CommanModel()
        if (this.products[position].node.images.edges.size > 0) {
            model.imageurl = this.products[position].node.images.edges[0].node.transformedSrc
        }
        holder?.binding?.features = SplashViewModel.featuresModel
        holder.binding!!.commondata = model
        holder.binding!!.clickproduct = Product(position)
        //holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun getDiscount(regular: Double, special: Double): Int {
        return ((regular - special) / regular * 100).toInt()
    }

    inner class Product(var position: Int) {
        fun productClick(view: View, data: ListData) {
            val productintent = Intent(view.context, ProductView::class.java)
            productintent.putExtra("ID", data.product!!.id.toString())
            productintent.putExtra("tittle", data.textdata)
            productintent.putExtra("product", data.product)
            productintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            productintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            productintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            view.context.startActivity(productintent)
            Constant.activityTransition(view.context)
        }


        fun wishListAdd(view: View, data: ListData) {
            if ((activity as ProductList).productListModel?.setWishList(data.product?.id.toString())!!) {
                Toast.makeText(view.context, view.context.resources.getString(R.string.successwish), Toast.LENGTH_LONG).show()
                data.addtowish = view.context.resources.getString(R.string.alreadyinwish)
                var wishlistData = JSONObject()
                wishlistData.put("id", data.product?.id.toString())
                wishlistData.put("quantity", 1)
                whilistArray.put(wishlistData.toString())
                Constant.logAddToWishlistEvent(whilistArray.toString(), data.product?.id.toString(), "product", data.product?.variants?.edges?.get(0)?.node?.presentmentPrices?.edges?.get(0)?.node?.price?.currencyCode?.toString(), data.product?.variants?.edges?.get(0)?.node?.presentmentPrices?.edges?.get(0)?.node?.price?.amount?.toDouble()
                        ?: 0.0, activity ?: Activity())

                if (SplashViewModel.featuresModel.firebaseEvents) {
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST) {
                        param(FirebaseAnalytics.Param.ITEM_ID, data.product?.id.toString())
                        param(FirebaseAnalytics.Param.QUANTITY, 1)
                    }
                }

            } else {
                (activity as ProductList).productListModel?.deleteData(data.product?.id.toString())
                data!!.addtowish = view.context.resources.getString(R.string.addtowish)
            }
            notifyDataSetChanged()
            (activity as ProductList).invalidateOptionsMenu()
        }

        fun addCart(view: View, data: ListData) {
            var customQuickAddActivity = QuickAddActivity(context = activity!!, theme = R.style.WideDialogFull, product_id = data.product!!.id.toString(), repository = repository!!)
            customQuickAddActivity.show()
        }
    }
}