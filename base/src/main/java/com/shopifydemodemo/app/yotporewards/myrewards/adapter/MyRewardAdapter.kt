package com.shopifydemodemo.app.yotporewards.myrewards.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.MyApplication.Companion.context
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MyrewardItemBinding
import com.shopifydemodemo.app.yotporewards.myrewards.model.HistoryItem
import com.google.firebase.database.core.Context
import javax.inject.Inject


class MyRewardAdapter @Inject constructor() : RecyclerView.Adapter<MyRewardAdapter.MyRewardViewHolder>() {
    var context:Context?=null
    var historyItems: List<HistoryItem>? = null
    fun setData(historyItems: List<HistoryItem>) {
        this.historyItems = historyItems
    }

    class MyRewardViewHolder : RecyclerView.ViewHolder {
        var binding: MyrewardItemBinding
        constructor(itemView: MyrewardItemBinding) : super(itemView.root) {
            this.binding = itemView
            binding.statustxt.textSize=15f
            binding.statustxt.setOnClickListener {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text",binding.statustxt.text)
                Toast.makeText(context,"code copied",Toast.LENGTH_SHORT).show()
                clipboard.setPrimaryClip(clip)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRewardViewHolder {
        var view = DataBindingUtil.inflate<MyrewardItemBinding>(LayoutInflater.from(parent.context), R.layout.myreward_item, parent, false)
        return MyRewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyRewardViewHolder, position: Int) {
        holder.binding.historyItem = historyItems?.get(position)
    }

    override fun getItemCount(): Int {
        return historyItems?.size ?: 0
    }
}