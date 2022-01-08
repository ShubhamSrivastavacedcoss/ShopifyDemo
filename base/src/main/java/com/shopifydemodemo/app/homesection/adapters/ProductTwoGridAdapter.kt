package com.shopifydemodemo.app.homesection.adapters

import android.app.Activity
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.basesection.models.ListData
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.customviews.MageNativeTextView
import com.shopifydemodemo.app.databinding.ProductGridItemsBinding
import com.shopifydemodemo.app.repositories.Repository
import com.shopifydemodemo.app.utils.CurrencyFormatter
import org.json.JSONObject
import java.math.BigDecimal

class ProductTwoGridAdapter : RecyclerView.Adapter<ProductTwoGridAdapter.ProductGridItems>() {
    private var layoutInflater: LayoutInflater? = null
    private var products: List<Storefront.Product>? = null
    private var activity: Activity? = null
    var presentmentcurrency: String? = null
    private val TAG = "ProductSliderListAdapte"
    var jsonObject: JSONObject? = null
    lateinit var repository: Repository
    fun setData(
        products: List<Storefront.Product>?,
        activity: Activity,
        jsonObject: JSONObject,
        repository: Repository
    ) {
        this.products = products
        this.activity = activity
        this.jsonObject = jsonObject
        this.repository = repository
    }

    init {
        setHasStableIds(true)
    }


    class ProductGridItems : RecyclerView.ViewHolder {
        var binding: ProductGridItemsBinding

        constructor(itemView: ProductGridItemsBinding) : super(itemView.root) {
            this.binding = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductGridItems {
        var binding = DataBindingUtil.inflate<ProductGridItemsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.product_grid_items,
            parent,
            false
        ) as ProductGridItemsBinding
        when (jsonObject!!.getString("item_shape")) {
            "square" -> {
                val item_border_color = JSONObject(jsonObject!!.getString("item_border_color"))
                binding.card.radius = 0f
                binding.card.cardElevation = 0f
                binding.card.useCompatPadding = true
                binding.card.setCardBackgroundColor(Color.parseColor(item_border_color.getString("color")))
//                var layout = binding.main.layoutParams as FrameLayout.LayoutParams
//                layout.setMargins(0, 0, 0, 2)
            }
        }
        if (jsonObject!!.getString("item_border").equals("1")) {
            val item_border_color = JSONObject(jsonObject!!.getString("item_border_color"))
            if (jsonObject!!.getString("item_shape").equals("rounded")) {
//                var gradientDrawable = GradientDrawable()
//                gradientDrawable.shape = GradientDrawable.RECTANGLE
//                gradientDrawable.cornerRadius = 16f
//                gradientDrawable.setStroke(3, Color.parseColor(item_border_color.getString("color")))
//                binding.card.useCompatPadding = false
                binding.card.elevation = 3f
                binding.card.setCardBackgroundColor(Color.parseColor(item_border_color.getString("color")))
                //   binding.card.background = gradientDrawable
            }
        }
        return ProductGridItems(binding)
    }

    override fun onBindViewHolder(item: ProductGridItems, position: Int) {
        val variant = products?.get(position)!!.variants.edges.get(0).node
        val data = ListData()
        var view: View
        var card: CardView
        var main: ConstraintLayout
        var tittle: MageNativeTextView
        var price: MageNativeTextView
        var special: MageNativeTextView
        data.product = products?.get(position)
        data.textdata = products?.get(position)?.title.toString().trim()
        if (presentmentcurrency == "nopresentmentcurrency") {
            data.regularprice = CurrencyFormatter.setsymbol(
                variant!!.priceV2.amount,
                variant.priceV2.currencyCode.toString()
            )
            if (variant.compareAtPriceV2 != null) {
                val special = java.lang.Double.valueOf(variant.compareAtPriceV2.amount)
                val regular = java.lang.Double.valueOf(variant.priceV2.amount)
                if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                    data.regularprice = CurrencyFormatter.setsymbol(
                        variant.compareAtPriceV2.amount,
                        variant.compareAtPriceV2.currencyCode.toString()
                    )
                    data.specialprice = CurrencyFormatter.setsymbol(
                        variant.priceV2.amount,
                        variant.priceV2.currencyCode.toString()
                    )
                } else {
                    data.regularprice = CurrencyFormatter.setsymbol(
                        variant.priceV2.amount,
                        variant.priceV2.currencyCode.toString()
                    )
                    data.specialprice = CurrencyFormatter.setsymbol(
                        variant.compareAtPriceV2.amount,
                        variant.compareAtPriceV2.currencyCode.toString()
                    )
                }
                item.binding.regularprice.paintFlags =
                    item.binding.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                item.binding.specialprice.visibility = View.VISIBLE
            } else {
                item.binding.specialprice.visibility = View.GONE
                item.binding.regularprice.paintFlags =
                    item.binding.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        } else {
            val edge = variant!!.presentmentPrices.edges[0]
            data.regularprice = CurrencyFormatter.setsymbol(
                edge?.node?.price?.amount!!,
                edge?.node?.price?.currencyCode.toString()
            )
            if (variant.compareAtPriceV2 != null) {
                val special = java.lang.Double.valueOf(edge.node.compareAtPrice.amount)
                val regular = java.lang.Double.valueOf(edge.node.price.amount)
                if (BigDecimal.valueOf(special).compareTo(BigDecimal.valueOf(regular)) == 1) {
                    data.regularprice = CurrencyFormatter.setsymbol(
                        edge.node.compareAtPrice.amount,
                        edge.node.compareAtPrice.currencyCode.toString()
                    )
                    data.specialprice = CurrencyFormatter.setsymbol(
                        edge.node.price.amount,
                        edge.node.price.currencyCode.toString()
                    )

                } else {
                    data.regularprice = CurrencyFormatter.setsymbol(
                        edge.node.price.amount,
                        edge.node.price.currencyCode.toString()
                    )
                    data.specialprice = CurrencyFormatter.setsymbol(
                        edge.node.compareAtPrice.amount,
                        edge.node.compareAtPrice.currencyCode.toString()
                    )
                }
                item.binding.regularprice.paintFlags =
                    item.binding.regularprice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                item.binding.specialprice.visibility = View.VISIBLE
            } else {
                item.binding.specialprice.visibility = View.GONE
                item.binding.regularprice.paintFlags =
                    item.binding.regularprice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
        val model = CommanModel()
        if (products?.get(position)?.images?.edges?.size ?: 0 >= 1) {
            model.imageurl = products?.get(position)?.images?.edges?.get(0)?.node?.transformedSrc
        }
        item.binding.listdata = data
        item.binding.commondata = model
        item.binding.clickproduct = ProductSliderAdapter().Product(repository, activity!!)
        val params: ConstraintLayout.LayoutParams
        view = item.binding.main
        card = item.binding.card
        main = item.binding.main
        tittle = item.binding.name
        price = item.binding.regularprice
        special = item.binding.specialprice
        params = item.binding.name.layoutParams as ConstraintLayout.LayoutParams
        val priceparams = item.binding.pricesection.layoutParams as ConstraintLayout.LayoutParams
        var alignment: String
        if (jsonObject!!.has("item_text_alignment")) {
            alignment = jsonObject!!.getString("item_text_alignment")
        } else {
            alignment = jsonObject!!.getString("item_alignment")
        }
        when (alignment) {
            "right" -> {
                params.endToEnd = ConstraintSet.PARENT_ID
                priceparams.endToEnd = ConstraintSet.PARENT_ID
                params.startToStart = ConstraintSet.GONE
                priceparams.startToStart = ConstraintSet.GONE
            }
            "center" -> {
                params.endToEnd = ConstraintSet.PARENT_ID
                priceparams.endToEnd = ConstraintSet.PARENT_ID
                params.startToStart = ConstraintSet.PARENT_ID
                priceparams.startToStart = ConstraintSet.PARENT_ID
            }
        }
        var tittlevisibility: Int = View.GONE
        if (jsonObject!!.getString("item_title").equals("1")) {
            tittlevisibility = View.VISIBLE
        } else {
            tittlevisibility = View.GONE
        }
        var productpricevisibility: Int = View.GONE
        var specialpricevisibility: Int = View.GONE
        if (jsonObject!!.getString("item_price").equals("1")) {
            productpricevisibility = View.VISIBLE
            if (jsonObject!!.getString("item_compare_at_price").equals("1")) {
                specialpricevisibility = View.VISIBLE
            } else {
                specialpricevisibility = View.GONE
            }
        } else {
            productpricevisibility = View.GONE
            specialpricevisibility = View.GONE
        }
        if (tittlevisibility == View.GONE && productpricevisibility == View.GONE) {
            var parms = item.binding.card.layoutParams
            var pars = item.binding.image.layoutParams
            parms.height = 700
            pars.height = 700
            item.binding.nameandpricesection.visibility = View.GONE
        }
        item.binding.name.visibility = tittlevisibility
        item.binding.regularprice.visibility = productpricevisibility
        item.binding.specialprice.visibility = specialpricevisibility
        var cell_background_color = JSONObject(jsonObject!!.getString("cell_background_color"))

        var item_title_color = JSONObject(jsonObject!!.getString("item_title_color"))
        var item_price_color = JSONObject(jsonObject!!.getString("item_price_color"))
        var item_compare_at_price_color =
            JSONObject(jsonObject!!.getString("item_compare_at_price_color"))
        view.setBackgroundColor(Color.parseColor(cell_background_color.getString("color")))
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

        if (SplashViewModel.featuresModel.outOfStock!!) {
            if (!products?.get(position)!!.availableForSale) {
                item?.binding?.outOfStock?.visibility = View.VISIBLE
            } else {
                item?.binding?.outOfStock?.visibility = View.GONE
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return products!!.size
    }

}
