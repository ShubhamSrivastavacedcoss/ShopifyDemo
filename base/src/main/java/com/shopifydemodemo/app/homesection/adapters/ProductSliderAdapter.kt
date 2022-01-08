package com.shopifydemodemo.app.homesection.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MSlideritemoneBinding
import com.shopifydemodemo.app.databinding.MSlideritemtwoBinding
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.basesection.models.ListData
import com.shopifydemodemo.app.customviews.MageNativeTextView
import com.shopifydemodemo.app.homesection.viewholders.SliderItemTypeOne
import com.shopifydemodemo.app.productsection.activities.ProductView
import com.shopifydemodemo.app.quickadd_section.activities.QuickAddActivity
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.CurrencyFormatter
import kotlinx.android.synthetic.main.m_trial.*
import org.json.JSONObject
import java.math.BigDecimal
import javax.inject.Inject

class ProductSliderAdapter @Inject
constructor() : RecyclerView.Adapter<SliderItemTypeOne>() {
    private var layoutInflater: LayoutInflater? = null
    private var products: List<Storefront.ProductEdge>? = null
    private var activity: Activity? = null
    var presentmentcurrency: String? = null
    var jsonObject: JSONObject? = null
    lateinit var repository: Repository
    fun setData(products: List<Storefront.ProductEdge>?, activity: Activity, jsonObject: JSONObject, repository: Repository) {
        this.products = products
        this.activity = activity
        this.jsonObject = jsonObject
        this.repository = repository
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderItemTypeOne {

        when (jsonObject!!.getString("item_shape")) {
            "square" -> {
                var binding = DataBindingUtil.inflate<MSlideritemtwoBinding>(LayoutInflater.from(parent.context), R.layout.m_slideritemtwo, parent, false)
                return SliderItemTypeOne(binding)
            }
            else -> {
                var binding = DataBindingUtil.inflate<MSlideritemoneBinding>(layoutInflater!!, R.layout.m_slideritemone, parent, false)
                return SliderItemTypeOne(binding)
            }
        }
    }

    override fun onBindViewHolder(item: SliderItemTypeOne, position: Int) {
        val variant = products?.get(position)?.node?.variants?.edges?.get(0)?.node
        val data = ListData()
        var view: View
        var card: View
        var tittle: MageNativeTextView
        var price: MageNativeTextView
        var special: MageNativeTextView
        data.product = products?.get(position)?.node
        data.textdata = products?.get(position)?.node?.title.toString().trim()
        if (presentmentcurrency == "nopresentmentcurrency") {
            data.regularprice = CurrencyFormatter.setsymbol(variant!!.priceV2.amount, variant.priceV2.currencyCode.toString())
            if (variant.compareAtPriceV2 != null) {
                val special = java.lang.Double.valueOf(variant.compareAtPriceV2.amount)
                val regular = java.lang.Double.valueOf(variant.priceV2.amount)
                if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                    data.regularprice = CurrencyFormatter.setsymbol(variant.compareAtPriceV2.amount, variant.compareAtPriceV2.currencyCode.toString())
                    data.specialprice = CurrencyFormatter.setsymbol(variant.priceV2.amount, variant.priceV2.currencyCode.toString())
                    when (jsonObject!!.getString("item_shape")) {
                        "square" -> {
                            item.bindingtwo!!.regularprice.paintFlags = item!!.bindingtwo!!.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            item.bindingtwo!!.specialprice.visibility = View.VISIBLE
                        }
                        else -> {
                            item!!.bindingtwo!!.regularprice.paintFlags = item!!.bindingtwo!!.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            item!!.bindingtwo!!.specialprice.visibility = View.VISIBLE
                        }
                    }
                } else {
                    when (jsonObject!!.getString("item_shape")) {
                        "square" -> {
                            item.bindingtwo!!.specialprice.visibility = View.GONE
                            item.bindingtwo!!.regularprice.paintFlags = item.bindingtwo!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        }
                        else -> {
                            item.binding.specialprice.visibility = View.GONE
                            item.binding.regularprice.paintFlags = item.binding.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        }
                    }

                }
            } else {
                when (jsonObject!!.getString("item_shape")) {
                    "square" -> {
                        item.bindingtwo!!.specialprice.visibility = View.GONE
                        item.bindingtwo!!.regularprice.paintFlags = item!!.bindingtwo!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                    else -> {
                        item!!.bindingtwo!!.specialprice.visibility = View.GONE
                        item!!.bindingtwo!!.regularprice.paintFlags = item!!.bindingtwo!!.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }
            }
        } else {
            val edge = variant!!.presentmentPrices.edges[0]
            data.regularprice = CurrencyFormatter.setsymbol(edge!!.node.price.amount, edge.node.price.currencyCode.toString())
            if (variant.compareAtPriceV2 != null) {
                val special = java.lang.Double.valueOf(edge.node.compareAtPrice.amount)
                val regular = java.lang.Double.valueOf(edge.node.price.amount)
                if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                    data.regularprice = CurrencyFormatter.setsymbol(edge.node.compareAtPrice.amount, edge.node.compareAtPrice.currencyCode.toString())
                    data.specialprice = CurrencyFormatter.setsymbol(edge.node.price.amount, edge.node.price.currencyCode.toString())
                    when (jsonObject!!.getString("item_shape")) {
                        "square" -> {
                            item.bindingtwo.regularprice.paintFlags = item.bindingtwo.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            item.bindingtwo.specialprice.visibility = View.VISIBLE
                        }
                        else -> {
                            item.binding.regularprice.paintFlags = item.binding.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            item.binding.specialprice.visibility = View.VISIBLE
                        }
                    }
                } else {
                    when (jsonObject!!.getString("item_shape")) {
                        "square" -> {
                            item.bindingtwo.specialprice.visibility = View.GONE
                            item.bindingtwo.regularprice.paintFlags = item.bindingtwo.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        }
                        else -> {
                            item.binding.specialprice.visibility = View.GONE
                            item.binding.regularprice.paintFlags = item.binding.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        }
                    }
                }
            } else {
                when (jsonObject!!.getString("item_shape")) {
                    "square" -> {
                        item.bindingtwo.specialprice.visibility = View.GONE
                        item.bindingtwo.regularprice.paintFlags = item.binding.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                    else -> {
                        item.binding.specialprice.visibility = View.GONE
                        item.binding.regularprice.paintFlags = item.binding.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }
            }
        }
        val model = CommanModel()
        model.imageurl = products?.get(position)?.node?.images?.edges?.get(0)?.node?.transformedSrc
        when (jsonObject!!.getString("item_shape")) {
            "square" -> {
                item.bindingtwo.listdata = data
                item.bindingtwo.commondata = model
                item.bindingtwo.clickproduct = Product(repository, activity!!)
            }
            else -> {
                item.binding.listdata = data
                item.binding.commondata = model
                item.binding.clickproduct = Product(repository, activity!!)
            }
        }
        val params: ConstraintLayout.LayoutParams
        when (jsonObject!!.getString("item_shape")) {
            "square" -> {
                view = item.bindingtwo.main
                card = item.bindingtwo.card
                tittle = item.bindingtwo.name
                price = item.bindingtwo.regularprice
                special = item.bindingtwo.specialprice
                params = item.bindingtwo.nameandpricesection.layoutParams as ConstraintLayout.LayoutParams
            }
            else -> {
                view = item.binding.main
                card = item.binding.card
                tittle = item.binding.name
                price = item.binding.regularprice
                special = item.binding.specialprice
                params = item.binding.nameandpricesection.layoutParams as ConstraintLayout.LayoutParams
            }
        }
        var alignment: String
        if (jsonObject!!.has("item_text_alignment")) {
            alignment = jsonObject!!.getString("item_text_alignment")
        } else {
            alignment = jsonObject!!.getString("item_alignment")
        }
        when (alignment) {
            "right" -> {
                params.endToEnd = ConstraintSet.PARENT_ID
                params.startToStart = ConstraintSet.GONE
            }
            "center" -> {
                params.endToEnd = ConstraintSet.PARENT_ID
                params.startToStart = ConstraintSet.PARENT_ID
            }
        }
        var tittlevisibility: Int = View.GONE
        if (jsonObject!!.getString("item_title").equals("1")) {
            tittlevisibility = View.VISIBLE
        } else {
            tittlevisibility = View.GONE
        }
        var productpricevisibility: Int = View.GONE
        if (jsonObject!!.getString("item_price").equals("1")) {
            productpricevisibility = View.VISIBLE
        } else {
            productpricevisibility = View.GONE
        }
        var specialpricevisibility: Int = View.GONE
        if (jsonObject!!.getString("item_compare_at_price").equals("1")) {
            specialpricevisibility = View.VISIBLE
        } else {
            specialpricevisibility = View.GONE
        }
        when (jsonObject!!.getString("item_shape")) {
            "square" -> {
                item.bindingtwo.name.visibility = tittlevisibility
                item.bindingtwo.regularprice.visibility = productpricevisibility
                item.bindingtwo.specialprice.visibility = specialpricevisibility
            }
            else -> {
                item.binding.name.visibility = tittlevisibility
                item.binding.regularprice.visibility = productpricevisibility
                item.binding.specialprice.visibility = specialpricevisibility
            }
        }
        var cell_background_color = JSONObject(jsonObject!!.getString("cell_background_color"))
        var item_border_color = JSONObject(jsonObject!!.getString("item_border_color"))
        var item_title_color = JSONObject(jsonObject!!.getString("item_title_color"))
        var item_price_color = JSONObject(jsonObject!!.getString("item_price_color"))
        var item_compare_at_price_color = JSONObject(jsonObject!!.getString("item_compare_at_price_color"))
        view.setBackgroundColor(Color.parseColor(cell_background_color.getString("color")))
        card.setBackgroundColor(Color.parseColor(item_border_color.getString("color")))
        tittle.setTextColor(Color.parseColor(item_title_color.getString("color")))
        price.setTextColor(Color.parseColor(item_price_color.getString("color")))
        special.setTextColor(Color.parseColor(item_compare_at_price_color.getString("color")))
        val face: Typeface
        when (jsonObject!!.getString("item_title_font_weight")) {
            "bold" -> {
                face = Typeface.createFromAsset(activity!!.assets, "fonts/cairobold.ttf");
            }
            else -> {
                face = Typeface.createFromAsset(activity!!.assets, "fonts/cairoregular.ttf");
            }
        }
        tittle.setTypeface(face)
        if (jsonObject!!.getString("item_title_font_style").equals("italic")) {
            tittle.setTypeface(tittle.getTypeface(), Typeface.ITALIC);
        }
        val priceface: Typeface
        when (jsonObject!!.getString("header_subtitle_font_weight")) {
            "bold" -> {
                priceface = Typeface.createFromAsset(activity!!.assets, "fonts/cairobold.ttf");
            }
            else -> {
                priceface = Typeface.createFromAsset(activity!!.assets, "fonts/cairoregular.ttf");
            }
        }
        price.setTypeface(priceface)
        if (jsonObject!!.getString("item_price_font_style").equals("italic")) {
            price.setTypeface(price.getTypeface(), Typeface.ITALIC);
        }
        val specialpriceface: Typeface
        when (jsonObject!!.getString("item_compare_at_price_font_weight")) {
            "bold" -> {
                specialpriceface = Typeface.createFromAsset(activity!!.assets, "fonts/cairobold.ttf");
            }
            else -> {
                specialpriceface = Typeface.createFromAsset(activity!!.assets, "fonts/cairoregular.ttf");
            }
        }
        special.setTypeface(specialpriceface)
        if (jsonObject!!.getString("item_compare_at_price_font_style").equals("italic")) {
            special.setTypeface(special.getTypeface(), Typeface.ITALIC);
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return products!!.size
    }


    inner class Product(var repository: Repository, var activity: Activity) {
        fun productClick(view: View, data: ListData) {
            val productintent = Intent(view.context, ProductView::class.java)
            productintent.putExtra("ID", data.product!!.id.toString())
            productintent.putExtra("tittle", data.textdata)
            productintent.putExtra("product", data.product)
            view.context.startActivity(productintent)
            Constant.activityTransition(view.context)
        }

        fun addCart(view: View, data: ListData) {
            var customQuickAddActivity = QuickAddActivity(context = activity!!, theme = R.style.WideDialogFull, product_id = data.product!!.id.toString(), repository = repository!!)
            customQuickAddActivity.show()
        }
    }
}
