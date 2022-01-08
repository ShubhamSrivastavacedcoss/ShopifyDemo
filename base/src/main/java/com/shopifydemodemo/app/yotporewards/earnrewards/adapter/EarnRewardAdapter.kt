package com.shopifydemodemo.app.yotporewards.earnrewards.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.EarnrewardItemBinding
import com.shopifydemodemo.app.yotporewards.earnrewards.model.EarnRewardModelItem
import javax.inject.Inject

class EarnRewardAdapter @Inject constructor() : RecyclerView.Adapter<EarnRewardAdapter.EarnRewardViewHolder>() {
    private var earnRewardModel: List<EarnRewardModelItem>? = null
    private var clickEarnCallback: ClickEarnCallback? = null
    fun setData(earnRewardModel: List<EarnRewardModelItem>?, clickEarnCallback: ClickEarnCallback) {
        this.earnRewardModel = earnRewardModel
        this.clickEarnCallback = clickEarnCallback
    }

    class EarnRewardViewHolder : RecyclerView.ViewHolder {
        var binding: EarnrewardItemBinding

        constructor(itemView: EarnrewardItemBinding) : super(itemView.root) {
            this.binding = itemView
        }
    }

    interface ClickEarnCallback {
        fun earnRewardCallback(earnRewardModelItem: EarnRewardModelItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarnRewardViewHolder {
        var view = DataBindingUtil.inflate<EarnrewardItemBinding>(LayoutInflater.from(parent.context), R.layout.earnreward_item, parent, false)
        return EarnRewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: EarnRewardViewHolder, position: Int) {
        holder.binding.earnreward = earnRewardModel?.get(position)
        holder.binding.rewardpoint.setOnClickListener {
            clickEarnCallback?.earnRewardCallback(earnRewardModel?.get(position)!!)
        }
        if (earnRewardModel?.get(position)?.icon.equals("fa-inr")) {
            holder.binding.cardIcon.setImageDrawable(holder.binding.cardIcon.context.getDrawable(R.drawable.rupee))
        } else if (earnRewardModel?.get(position)?.icon.equals("fa-heart")) {
            holder.binding.cardIcon.setImageDrawable(holder.binding.cardIcon.context.getDrawable(R.drawable.heart_image))
        } else if (earnRewardModel?.get(position)?.icon.equals("fa-user")) {
            holder.binding.cardIcon.setImageDrawable(holder.binding.cardIcon.context.getDrawable(R.drawable.user_reward))
        }
    }

    override fun getItemCount(): Int {
        return earnRewardModel?.size ?: 0
    }
}