package com.shopifydemodemo.app.personalised.adapters

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
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.basesection.models.ListData
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.databinding.MPersonalisedBinding
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

class PersonalisedAdapter @Inject
constructor() : RecyclerView.Adapter<ProductItem>() {
    private var layoutInflater: LayoutInflater? = null
    lateinit var products: List<Storefront.Product>
    private var activity: Activity? = null
    private var repository: Repository? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var presentmentcurrency: String? = null
    var whilistArray = JSONArray()
    fun setData(products: List<Storefront.Product>, activity: Activity, repository: Repository) {
        this.products = products
        this.activity = activity
        this.repository = repository
        firebaseAnalytics = Firebase.analytics
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItem {
        val binding = DataBindingUtil.inflate<MPersonalisedBinding>(LayoutInflater.from(parent.context), R.layout.m_personalised, parent, false)
        return ProductItem(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ProductItem, position: Int) {

        try {
            var pro = products.get(position)
            val variant = pro.variants.edges[0].node
            val data = ListData()
            Log.i("MageNative", "Product ID" + pro.id)
            data.product = pro
            data.textdata = pro.title
            data.description = pro.description
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
                    holder.personalbinding!!.regularprice.textSize = 13f
                    var typeface = Typeface.createFromAsset(holder?.personalbinding?.regularprice?.context?.assets, "fonts/normal.ttf")
                    holder.personalbinding!!.regularprice.setTypeface(typeface)
                    holder.personalbinding!!.regularprice.paintFlags = holder.personalbinding!!.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    holder.personalbinding!!.specialprice.visibility = View.VISIBLE
                    //holder.personalbinding!!.offertext.visibility = View.VISIBLE
                    //holder.personalbinding!!.offertext.setTextColor(holder.personalbinding!!.offertext.context!!.resources.getColor(R.color.green))
                } else {
                    holder.personalbinding!!.specialprice.visibility = View.GONE
                    //holder.personalbinding!!.offertext.visibility = View.GONE
                    holder.personalbinding!!.regularprice.textSize = 15f
                    var typeface = Typeface.createFromAsset(holder?.personalbinding?.regularprice?.context?.assets, "fonts/bold.ttf")
                    holder.personalbinding!!.regularprice.setTypeface(typeface)
                    holder.personalbinding!!.regularprice.paintFlags = holder.personalbinding!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
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
                    holder.personalbinding!!.regularprice.textSize = 13f
                    var typeface = Typeface.createFromAsset(holder?.personalbinding?.regularprice?.context?.assets, "fonts/normal.ttf")
                    holder.personalbinding!!.regularprice.setTypeface(typeface)
                    holder.personalbinding!!.regularprice.paintFlags = holder.personalbinding!!.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    holder.personalbinding!!.specialprice.visibility = View.VISIBLE
                    //    holder.personalbinding!!.offertext.visibility = View.VISIBLE
                    //   holder.personalbinding!!.offertext.setTextColor(holder.personalbinding!!.offertext.context!!.resources.getColor(R.color.green))
                } else {
                    holder.personalbinding!!.regularprice.textSize = 15f
                    var typeface = Typeface.createFromAsset(holder?.personalbinding?.regularprice?.context?.assets, "fonts/bold.ttf")
                    holder.personalbinding!!.regularprice.setTypeface(typeface)
                    holder.personalbinding!!.specialprice.visibility = View.GONE
                    // holder.personalbinding!!.offertext.visibility = View.GONE
                    holder.personalbinding!!.regularprice.paintFlags = holder.personalbinding!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }

            if (SplashViewModel.featuresModel.in_app_wishlist) {
                if ((holder?.personalbinding?.wishlistBut?.context as NewBaseActivity).leftMenuViewModel?.isInwishList(data.product?.id.toString())!!) {
                    data!!.addtowish = holder?.personalbinding?.wishlistBut?.context?.resources?.getString(R.string.alreadyinwish)
                    Glide.with((holder?.personalbinding?.wishlistBut?.context as NewBaseActivity)!!)
                            .load(R.drawable.wishlist_selected)
                            .into(holder?.personalbinding?.wishlistBut!!)

                    Glide.with((holder?.personalbinding?.wishlistBut?.context as NewBaseActivity)!!)
                            .load(R.drawable.wishlist_selected)
                            .into(holder?.personalbinding?.wishBut!!)
                } else {
                    data!!.addtowish = holder?.personalbinding?.wishlistBut?.context?.resources?.getString(R.string.addtowish)
                    Glide.with((holder?.personalbinding?.wishlistBut?.context as NewBaseActivity)!!)
                            .load(R.drawable.wishlist_icon)
                            .into(holder?.personalbinding?.wishlistBut!!)

                    Glide.with((holder?.personalbinding?.wishlistBut?.context as NewBaseActivity)!!)
                            .load(R.drawable.wishlist_icon)
                            .into(holder?.personalbinding?.wishBut!!)
                }
            }
            if (!SplashViewModel.featuresModel.in_app_wishlist && !SplashViewModel.featuresModel.addCartEnabled) {
                holder.personalbinding?.wishlistBut?.visibility = View.GONE
                holder?.personalbinding?.wishBut?.visibility = View.GONE
                holder?.personalbinding?.cartBut?.visibility = View.GONE
            } else if (SplashViewModel.featuresModel.in_app_wishlist && !SplashViewModel.featuresModel.addCartEnabled) {
                holder.personalbinding?.wishlistBut?.visibility = View.VISIBLE
                holder?.personalbinding?.wishBut?.visibility = View.GONE
                holder?.personalbinding?.cartBut?.visibility = View.GONE
            } else if (!SplashViewModel.featuresModel.in_app_wishlist && SplashViewModel.featuresModel.addCartEnabled) {
                holder.personalbinding?.wishlistBut?.visibility = View.GONE
                holder?.personalbinding?.wishBut?.visibility = View.GONE
                holder?.personalbinding?.cartBut?.visibility = View.VISIBLE
            } else if (SplashViewModel.featuresModel.in_app_wishlist && SplashViewModel.featuresModel.addCartEnabled) {
                holder.personalbinding?.wishlistBut?.visibility = View.GONE
                holder?.personalbinding?.wishBut?.visibility = View.VISIBLE
                holder?.personalbinding?.cartBut?.visibility = View.VISIBLE
            }

            holder?.personalbinding?.features = SplashViewModel.featuresModel
            holder.personalbinding!!.listdata = data
            val model = CommanModel()
            model.imageurl = pro?.images?.edges?.get(0)?.node?.transformedSrc
            holder.personalbinding!!.commondata = model
            holder.personalbinding!!.clickproduct = PersonalisedProduct(position)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun getDiscount(regular: Double, special: Double): Int {
        return ((regular - special) / regular * 100).toInt()
    }

    inner class PersonalisedProduct(var position: Int) {
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
            if ((view.context as NewBaseActivity).leftMenuViewModel?.setWishList(data.product?.id.toString())!!) {
                Toast.makeText(view.context, view.context.resources.getString(R.string.successwish), Toast.LENGTH_LONG).show()
                data.addtowish = view.context.resources.getString(R.string.alreadyinwish)
                var wishlistData = JSONObject()
                wishlistData.put("id", data.product?.id.toString())
                wishlistData.put("quantity", 1)
                whilistArray.put(wishlistData.toString())
                if (SplashViewModel.featuresModel.firebaseEvents) {
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST) {
                        param(FirebaseAnalytics.Param.ITEM_ID, data.product?.id.toString())
                        param(FirebaseAnalytics.Param.QUANTITY, 1)
                    }
                }
                Constant.logAddToWishlistEvent(whilistArray.toString(), data.product?.id.toString(), "product", data.product?.variants?.edges?.get(0)?.node?.presentmentPrices?.edges?.get(0)?.node?.price?.currencyCode?.toString(), data.product?.variants?.edges?.get(0)?.node?.presentmentPrices?.edges?.get(0)?.node?.price?.amount?.toDouble()
                        ?: 0.0, activity ?: Activity())
            } else {
                (view.context as NewBaseActivity).leftMenuViewModel?.deleteData(data.product?.id.toString())
                data!!.addtowish = view.context.resources.getString(R.string.addtowish)
            }
            notifyDataSetChanged()
            (view.context as NewBaseActivity).invalidateOptionsMenu()
        }

        fun addCart(view: View, data: ListData) {
            var customQuickAddActivity = QuickAddActivity(context = activity!!, activity = activity, theme = R.style.WideDialogFull, product_id = data.product!!.id.toString(), repository = repository!!)
            if (data.product?.variants?.edges?.size == 1) {
                customQuickAddActivity.addToCart(data.product?.variants?.edges?.get(0)?.node?.id.toString(), 1)
            } else {
                customQuickAddActivity.initView()
            }
        }
    }
}
