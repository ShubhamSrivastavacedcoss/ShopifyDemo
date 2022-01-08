package com.shopifydemodemo.app.quickadd_section.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.CustomVariantListitemBinding
import com.shopifydemodemo.app.productsection.models.VariantData
import com.shopifydemodemo.app.utils.CurrencyFormatter

class QuickVariantAdapter : RecyclerView.Adapter<QuickVariantAdapter.QuickVariantViewHolder>() {

    var variants: List<Storefront.ProductVariantEdge>? = null
    var context: Context? = null
    var presentmentcurrency: String? = null

    companion object {
        var itemClickVariant: ItemClick? = null
        var selectedPosition: Int = -1
    }

    fun setData(variants: List<Storefront.ProductVariantEdge>, context: Context, itemClick: ItemClick) {
        this.variants = variants
        itemClickVariant = itemClick
        this.context = context
    }

    interface ItemClick {
        fun variantSelection(variantData: Storefront.ProductVariantEdge)
    }

    class QuickVariantViewHolder : RecyclerView.ViewHolder {
        var customVariantListitemBinding: CustomVariantListitemBinding? = null

        constructor(itemView: CustomVariantListitemBinding) : super(itemView.root) {
            customVariantListitemBinding = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickVariantViewHolder {
        var binding = DataBindingUtil.inflate<CustomVariantListitemBinding>(LayoutInflater.from(parent.context), R.layout.custom_variant_listitem, null, false)
        return QuickVariantViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return variants!!.size
    }

    override fun onBindViewHolder(holder: QuickVariantViewHolder, position: Int) {
        val data = VariantData()
        data.position = position
        if (presentmentcurrency == "nopresentmentcurrency") {
            holder.customVariantListitemBinding?.price?.text = CurrencyFormatter.setsymbol(variants?.get(position)?.node?.priceV2?.amount!!, variants?.get(position)?.node?.priceV2?.currencyCode.toString())
        } else {
            val edge = variants?.get(position)?.node?.presentmentPrices?.edges?.get(0)
            holder.customVariantListitemBinding?.price?.text = CurrencyFormatter.setsymbol(edge?.node?.price?.amount!!, edge.node.price.currencyCode.toString())
        }
        data.variantimage = variants!![position]?.node?.image?.transformedSrc
        data.variant_id = variants!![position].node.id.toString()
        setVariants(data, holder, variants!![position].node.selectedOptions)
        var sdk: Int = android.os.Build.VERSION.SDK_INT;
        if (selectedPosition == position) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.customVariantListitemBinding?.mainview?.setBackgroundDrawable(ContextCompat.getDrawable(context!!, R.drawable.variant_select_bg));
            } else {
                holder.customVariantListitemBinding?.mainview?.setBackground(ContextCompat.getDrawable(context!!, R.drawable.variant_select_bg));
            }
        } else {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.customVariantListitemBinding?.mainview?.setBackgroundDrawable(ContextCompat.getDrawable(context!!, R.drawable.variant_default_bg));
            } else {
                holder.customVariantListitemBinding?.mainview?.setBackground(ContextCompat.getDrawable(context!!, R.drawable.variant_default_bg));
            }
        }
        holder.customVariantListitemBinding?.mainview?.setOnClickListener {
            itemClickVariant?.variantSelection(variants?.get(position)!!)
            selectedPosition = position
            notifyDataSetChanged()
        }
    }


    private fun setVariants(data: VariantData, holder: QuickVariantViewHolder, selectedOptions: List<Storefront.SelectedOption>) {
        try {
            val iterator1 = selectedOptions.iterator()
            var counter = 0
            var option: Storefront.SelectedOption
            while (iterator1.hasNext()) {
                counter = counter + 1
                option = iterator1.next()
                val finalvalue = option.name + " : " + option.value
                if (counter == 1) {
                    data.selectedoption_one = finalvalue
                }
                if (counter == 2) {
                    data.selectedoption_two = finalvalue
                }
                if (counter == 3) {
                    data.selectedoption_three = finalvalue
                }
                if (counter > 3) {
                    break
                }
            }
            holder.customVariantListitemBinding?.varaintData = data
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}