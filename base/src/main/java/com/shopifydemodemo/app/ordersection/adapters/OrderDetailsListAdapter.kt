package com.shopifydemodemo.app.ordersection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.databinding.OrderListItemBinding
import javax.inject.Inject

class OrderDetailsListAdapter @Inject constructor() : RecyclerView.Adapter<OrderDetailsListAdapter.OrderDetailsListViewHolder>() {
    private var orderlineItem: List<Storefront.OrderLineItemEdge>? = null

    fun setData(orderlineItem: List<Storefront.OrderLineItemEdge>?) {
        this.orderlineItem = orderlineItem
    }

    class OrderDetailsListViewHolder : RecyclerView.ViewHolder {
        var orderListItemBinding: OrderListItemBinding

        constructor(itemView: OrderListItemBinding) : super(itemView.root) {
            this.orderListItemBinding = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsListViewHolder {
        var view = DataBindingUtil.inflate<OrderListItemBinding>(LayoutInflater.from(parent.context), R.layout.order_list_item, parent, false)
        return OrderDetailsListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderlineItem?.size ?: 0
    }

    override fun onBindViewHolder(holder: OrderDetailsListViewHolder, position: Int) {
        var commanModel = CommanModel()
        commanModel.imageurl = orderlineItem?.get(position)?.node?.variant?.image?.originalSrc
        holder.orderListItemBinding.commondata = commanModel
        holder.orderListItemBinding.productName.text = orderlineItem?.get(position)?.node?.title
        holder.orderListItemBinding.productquantityTxt.text = orderlineItem?.get(position)?.node?.quantity.toString()
        var variantTitle = StringBuffer()
        for (i in 0 until orderlineItem?.get(position)?.node?.variant?.selectedOptions?.size!!) {
            if (variantTitle.length > 0) {
                variantTitle.append("/")
            }
            variantTitle.append(orderlineItem?.get(position)?.node?.variant?.selectedOptions?.get(i)?.value)
        }
        holder.orderListItemBinding.productvariant.text = variantTitle.toString()
    }
}