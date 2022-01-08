package com.shopifydemodemo.app.productsection.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Typeface
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.models.ListData
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.databinding.OptionmenuDialogBinding
import com.shopifydemodemo.app.databinding.ProductListItemBinding
import com.shopifydemodemo.app.productsection.activities.ProductList
import com.shopifydemodemo.app.productsection.activities.ProductView
import com.shopifydemodemo.app.quickadd_section.activities.QuickAddActivity
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.CurrencyFormatter
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import javax.inject.Inject


class ProductRecyclerListAdapter @Inject
constructor() : RecyclerView.Adapter<ProductRecyclerListAdapter.ProductRecyclerListViewHolder>() {
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

    class ProductRecyclerListViewHolder : RecyclerView.ViewHolder {
        var binding: ProductListItemBinding

        constructor(itemView: ProductListItemBinding) : super(itemView.root) {
            this.binding = itemView
        }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductRecyclerListViewHolder {
        val binding = DataBindingUtil.inflate<ProductListItemBinding>(LayoutInflater.from(parent.context), R.layout.product_list_item, parent, false)
        return ProductRecyclerListViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ProductRecyclerListViewHolder, position: Int) {
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
                holder.binding!!.regularprice.paintFlags = holder.binding!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
        holder.binding!!.name.textSize = 18f
        holder.binding!!.specialprice.textSize = 15f
        holder.binding!!.offertext.textSize = 12f
        if (SplashViewModel.featuresModel.in_app_wishlist) {
            if ((activity as ProductList).productListModel?.isInwishList(data.product?.id.toString())!!) {
                data!!.addtowish = activity?.resources?.getString(R.string.alreadyinwish)
            } else {
                data!!.addtowish = activity?.resources?.getString(R.string.addtowish)
            }
        }
        if (SplashViewModel.featuresModel.outOfStock!!) {
            if (!this.products[position].node.availableForSale) {
                holder?.binding?.outOfStock?.visibility = View.VISIBLE
            } else {
                holder?.binding?.outOfStock?.visibility = View.GONE
            }
        }
        if (this.products[position].node.images.edges.size > 0) {
            Glide.with(holder.binding.image.context)
                    .load(this.products[position].node.images.edges[0].node.transformedSrc)
                    .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder))
                    .into(holder.binding.image)
        }
        holder.binding!!.listdata = data
        holder?.binding?.features = SplashViewModel.featuresModel
        holder.binding!!.clickproduct = Product(position)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    private fun getPointOfView(view: View): Point? {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return Point(location[0], location[1])
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

        fun increase(view: View, data: ListData) {
            var customQuickAddActivity = QuickAddActivity(context = activity!!, activity = activity, theme = R.style.WideDialogFull, product_id = data.product!!.id.toString(), repository = repository!!)
            if (data.product?.variants?.edges?.size == 1) {
                customQuickAddActivity.addToCart(data.product?.variants?.edges?.get(0)?.node?.id.toString(), 1)
            } else {
                customQuickAddActivity.initView()
            }
        }

        fun menuClick(view: View, data: ListData) {
            var dialog = Dialog(activity as Context, R.style.WideDialogSmall)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            val wmlp = dialog.window!!.attributes
            wmlp.gravity = Gravity.TOP or Gravity.RIGHT
            wmlp.x = 30
            wmlp.y = getPointOfView(view)?.y!! - 50
            dialog.setCancelable(false)
            var binding = DataBindingUtil.inflate<OptionmenuDialogBinding>(LayoutInflater.from(activity), R.layout.optionmenu_dialog, null, false)
            dialog.setContentView(binding.root)
            if ((activity as ProductList).productListModel?.isInwishList(data.product?.id.toString())!!) {
                Glide.with(activity!!)
                        .load(R.drawable.wishlist_selected)
                        .into(binding?.wishlistIcon!!)
            } else {
                Glide.with(activity!!)
                        .load(R.drawable.wishlist_icon)
                        .into(binding?.wishlistIcon!!)
            }

            binding.wishlistsection.setOnClickListener {
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
                dialog.dismiss()
            }
            binding.sharesection.setOnClickListener {
                val shareString = activity?.resources?.getString(R.string.hey) + "  " + data.product!!.title + "  " + activity?.resources?.getString(R.string.on) + "  " + activity?.resources?.getString(R.string.app_name) + "\n" + data.product!!.onlineStoreUrl + "?pid=" + data.product!!.id.toString()
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, view.context.resources.getString(R.string.app_name))
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareString)
                view.context.startActivity(Intent.createChooser(shareIntent, view.context.resources.getString(R.string.share)))
                Constant.activityTransition(view.context)
                dialog.dismiss()
            }
            binding.notintrestedsection.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}
