package com.shopifydemodemo.app.productsection.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.CategorydesignsBinding
import com.shopifydemodemo.app.productsection.viewholders.CategoryHolder
import javax.inject.Inject

class CustomAdapters @Inject constructor() : RecyclerView.Adapter<CategoryHolder>() {

    private lateinit var alledges: MutableList<Storefront.StoreAvailabilityEdge>
    private var activity: Activity? = null

    fun setData(alledges: MutableList<Storefront.StoreAvailabilityEdge>, activity: Activity) {
        this.alledges = alledges
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding = DataBindingUtil.inflate<CategorydesignsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.categorydesigns,
            parent,
            false
        )
        return CategoryHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        if (alledges[position].node.available.toString().equals("true")) {
            holder.binding.checks.setImageResource(R.drawable.checkmark)
            holder.binding.rheading.text =
                holder.binding.rheading.resources.getString(R.string.pickupavailable)
            holder.binding.raddfirst.text = alledges[position].node.location.address.city
            holder.binding.raddsecond.text =
                alledges[position].node.location.address.address2 + " " + alledges[position].node.location.address.address1 + " " +
                        alledges[position].node.location.address.province + " " + alledges[position].node.location.address.city + " " + alledges[position].node.location.address.zip
            holder.binding.rpickuptime.text = alledges[position].node.pickUpTime
            holder.binding.rphonenumber.text = alledges[position].node.location.address.phone
        } else if (alledges[position].node.available.toString().equals("false")) {
            holder.binding.checks.setImageResource(R.drawable.cross)
            holder.binding.rheading.text =
                holder.binding.rheading.resources.getString(R.string.pickupavailablenot)
            holder.binding.raddfirst.text = alledges[position].node.location.address.city
            holder.binding.raddsecond.text =
                alledges[position].node.location.address.address2 + " " + alledges[position].node.location.address.address1 + " " +
                        alledges[position].node.location.address.province + " " + alledges[position].node.location.address.city + " " + alledges[position].node.location.address.zip
            holder.binding.rpickuptime.text = alledges[position].node.pickUpTime
            holder.binding.rphonenumber.text = alledges[position].node.location.address.phone
        }
    }

    override fun getItemCount(): Int {
        return alledges.size
    }
}
