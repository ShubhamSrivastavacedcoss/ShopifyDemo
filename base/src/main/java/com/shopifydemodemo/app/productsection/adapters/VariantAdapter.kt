package com.shopifydemodemo.app.productsection.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity.Companion.themeColor
import com.shopifydemodemo.app.databinding.SwatchesListItemBinding
import com.shopifydemodemo.app.productsection.viewholders.VariantItem

class VariantAdapter : RecyclerView.Adapter<VariantItem>() {
    private var variants: MutableList<String>? = null
    private var context: Context? = null
    private val TAG = "VariantAdapter"
    private var outofStockList: MutableList<String>? = null
    private var selectedPosition = -1
    private var optionName: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantItem {
        val binding = DataBindingUtil.inflate<SwatchesListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.swatches_list_item,
            parent,
            false
        )
        binding.variantName.textSize = 14f
        return VariantItem(binding)
    }

    companion object {
        var variantCallback: VariantCallback? = null
    }

    interface VariantCallback {
        fun clickVariant(variantName: String, optionName: String)
    }

    override fun onBindViewHolder(holder: VariantItem, position: Int) {
        try {
            holder.binding.variantName.text = variants?.get(position)
            if (selectedPosition == position) {
                holder.binding.variantCard.setCardBackgroundColor(Color.parseColor(themeColor))
                holder.binding.variantName.setTextColor(Color.WHITE)
                holder.binding.variantName.isEnabled = true
                holder.binding.variantName.tag = "selected"
            } else {
//                if (outofStockList?.contains(variants?.get(position))!!) {
////                    holder.binding.variantName.background =
////                            context?.resources?.getDrawable(R.drawable.unselect_variant_bg)
//                    holder.binding.variantName.setTextColor(Color.parseColor("#D3D3D3"))
//                    holder.binding.variantName.isEnabled = false
//                } else {
                holder.binding.variantCard.setCardBackgroundColor(Color.WHITE)
                holder.binding.variantName.setTextColor(Color.BLACK)
                holder.binding.variantName.isEnabled = true
//                }
                holder.binding.variantName.tag = "unselected"
            }

            holder.binding.variantName.setOnClickListener {
                if (it.tag.equals("unselected")) {
                    selectedPosition = position
                    variantCallback?.clickVariant(variants?.get(position) ?: "", optionName ?: "")
                    notifyDataSetChanged()
                }
            }
            holder.setIsRecyclable(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return variants?.size!!
    }

    fun setData(
        optionName: String,
        variants: MutableList<String>,
        outofStockList: MutableList<String>,
        context: Context,
        variantCallback_: VariantCallback
    ) {
        this.optionName = optionName
        this.variants = variants
        this.outofStockList = outofStockList
        variantCallback = variantCallback_
        this.context = context
    }

}
