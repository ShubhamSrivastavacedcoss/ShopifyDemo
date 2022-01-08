package com.shopifydemodemo.app.homesection.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MMultiplegridBinding
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.basesection.models.ListData
import com.shopifydemodemo.app.homesection.viewholders.SliderItemTypeOne
import com.shopifydemodemo.app.productsection.activities.ProductView
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.CurrencyFormatter
import kotlinx.android.synthetic.main.m_trial.*
import org.json.JSONObject
import java.math.BigDecimal
import javax.inject.Inject

class ProductGridAdapter @Inject
constructor() : RecyclerView.Adapter<SliderItemTypeOne>() {
    private var layoutInflater: LayoutInflater? = null
    private var products: List<Storefront.ProductEdge>? = null
    private var activity: Activity? = null
    var presentmentcurrency: String? = null
    var jsonObject: JSONObject? = null
    fun setData(products: List<Storefront.ProductEdge>?, activity: Activity, jsonObject: JSONObject) {
        this.products = products
        this.activity = activity
        this.jsonObject = jsonObject
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderItemTypeOne {
        var binding = DataBindingUtil.inflate<MMultiplegridBinding>(LayoutInflater.from(parent.context), R.layout.m_multiplegrid, parent, false)
        if (jsonObject!!.getString("item_shape").equals("square")) {
            binding.card.cardElevation = 0f
            binding.card.radius = 0f
        }
        var params = binding.nameandpricesection.layoutParams as ConstraintLayout.LayoutParams
        var alignment: String
        if (jsonObject!!.has("item_text_alignment")) {
            alignment = jsonObject!!.getString("item_text_alignment")
        } else {
            alignment = jsonObject!!.getString("item_alignment")
        }
        when (alignment) {
            "right" -> {
                binding.name.gravity = Gravity.END
                params.startToStart = ConstraintSet.GONE
                params.endToEnd = ConstraintSet.PARENT_ID
            }
            "left" -> {
                binding.name.gravity = Gravity.START
                params.startToStart = ConstraintSet.PARENT_ID
                params.endToEnd = ConstraintSet.GONE
            }
        }
        var tittlevisibility: Int = View.GONE
        if (jsonObject!!.getString("item_title").equals("1")) {
            var item_title_color = JSONObject(jsonObject!!.getString("item_title_color"))
            binding.name.setTextColor(Color.parseColor(item_title_color.getString("color")))
            tittlevisibility = View.VISIBLE
        } else {
            tittlevisibility = View.GONE
        }
        var productpricevisibility: Int = View.GONE
        if (jsonObject!!.getString("item_price").equals("1")) {
            var item_price_color = JSONObject(jsonObject!!.getString("item_price_color"))
            binding.regularprice.setTextColor(Color.parseColor(item_price_color.getString("color")))
            productpricevisibility = View.VISIBLE
        } else {
            productpricevisibility = View.GONE
        }
        var specialpricevisibility: Int = View.GONE
        if (jsonObject!!.getString("item_compare_at_price").equals("1")) {
            var item_compare_at_price_color = JSONObject(jsonObject!!.getString("item_compare_at_price_color"))
            binding.specialprice.setTextColor(Color.parseColor(item_compare_at_price_color.getString("color")))
            specialpricevisibility = View.VISIBLE
        } else {
            specialpricevisibility = View.GONE
        }
        binding.name.visibility = tittlevisibility
        binding.regularprice.visibility = productpricevisibility
        binding.specialprice.visibility = specialpricevisibility
        if (jsonObject!!.getString("item_border").equals("1")) {
            var item_border_color = JSONObject(jsonObject!!.getString("item_border_color"))
            binding.card.setCardBackgroundColor(Color.parseColor(item_border_color.getString("color")))
            val newLayoutParams = binding.main.layoutParams as FrameLayout.LayoutParams
            newLayoutParams.marginStart = 2
            newLayoutParams.marginEnd = 2
            newLayoutParams.topMargin = 2
            newLayoutParams.bottomMargin = 2
            binding.main.layoutParams = newLayoutParams
        }
        var cell_background_color = JSONObject(jsonObject!!.getString("cell_background_color"))
        binding.main.setBackgroundColor(Color.parseColor(cell_background_color.getString("color")))
        val face: Typeface
        when (jsonObject!!.getString("item_title_font_weight")) {
            "bold" -> {
                face = Typeface.createFromAsset(activity!!.assets, "fonts/cairobold.ttf");
            }
            else -> {
                face = Typeface.createFromAsset(activity!!.assets, "fonts/cairoregular.ttf");
            }
        }
        binding.name.setTypeface(face)
        if (jsonObject!!.getString("item_title_font_style").equals("italic")) {
            binding.name.setTypeface(binding.name.getTypeface(), Typeface.ITALIC);
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
        binding.regularprice.setTypeface(priceface)
        if (jsonObject!!.getString("item_price_font_style").equals("italic")) {
            binding.regularprice.setTypeface(binding.regularprice.getTypeface(), Typeface.ITALIC);
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
        binding.specialprice.setTypeface(specialpriceface)
        if (jsonObject!!.getString("item_compare_at_price_font_style").equals("italic")) {
            binding.specialprice.setTypeface(binding.specialprice.getTypeface(), Typeface.ITALIC);
        }
        binding.name.textSize = 11f
        binding.regularprice.textSize = 10f
        binding.specialprice.textSize = 10f
        return SliderItemTypeOne(binding)
    }

    override fun onBindViewHolder(item: SliderItemTypeOne, position: Int) {
        val variant = products?.get(position)?.node?.variants?.edges?.get(0)?.node
        val data = ListData()
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
                    item.gridbinding.regularprice.paintFlags = item.gridbinding.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    item.gridbinding.specialprice.visibility = View.VISIBLE
                } else {
                    item.gridbinding.regularprice.paintFlags = item.gridbinding.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    item.gridbinding.specialprice.visibility = View.VISIBLE
                }
            } else {
                item.gridbinding.regularprice.paintFlags = item.gridbinding.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                item.gridbinding.specialprice.visibility = View.VISIBLE
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
                    item.gridbinding.regularprice.paintFlags = item.gridbinding.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    item.gridbinding.specialprice.visibility = View.VISIBLE

                } else {
                    item.gridbinding.specialprice.visibility = View.GONE
                    item.gridbinding.regularprice.paintFlags = item.gridbinding.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            } else {
                item.gridbinding.specialprice.visibility = View.GONE
                item.gridbinding.regularprice.paintFlags = item.gridbinding.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
        val model = CommanModel()
        model.imageurl = products?.get(position)?.node?.images?.edges?.get(0)?.node?.transformedSrc
        item.gridbinding.listdata = data
        item.gridbinding.commondata = model
        item.gridbinding.clickproduct = Product()

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return products!!.size
    }


    inner class Product {
        fun productClick(view: View, data: ListData) {
            val productintent = Intent(view.context, ProductView::class.java)
            productintent.putExtra("ID", data.product!!.id.toString())
            productintent.putExtra("tittle", data.textdata)
            productintent.putExtra("product", data.product)
            view.context.startActivity(productintent)
            Constant.activityTransition(view.context)
        }
    }
}