package com.shopifydemodemo.app.yotporewards.withoutlogin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.RedeemPointItemBinding
import javax.inject.Inject

class RewardsPointAdapter @Inject constructor() : RecyclerView.Adapter<RewardsPointAdapter.RewardsPointViewHolder>() {
    private var titleList: MutableList<String>? = null
    private var valuesList: MutableList<String>? = null
    fun setData(titleList: MutableList<String>, valuesList: MutableList<String>) {
        this.titleList = titleList
        this.valuesList = valuesList
    }

    class RewardsPointViewHolder : RecyclerView.ViewHolder {
        var binding: RedeemPointItemBinding? = null

        constructor(itemView: RedeemPointItemBinding) : super(itemView.root) {
            this.binding = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsPointViewHolder {
        var view = DataBindingUtil.inflate<RedeemPointItemBinding>(LayoutInflater.from(parent.context), R.layout.redeem_point_item, parent, false)
        return RewardsPointViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardsPointViewHolder, position: Int) {
        holder.binding?.optiontitle?.text = titleList?.get(position)
        holder.binding?.optionpoint?.text = valuesList?.get(position)
    }

    override fun getItemCount(): Int {
        return titleList?.size ?: 0
    }
}