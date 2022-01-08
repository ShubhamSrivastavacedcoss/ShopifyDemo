package com.shopifydemodemo.app.cartsection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.LocationListItemBinding

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationListAdapter @Inject constructor() : RecyclerView.Adapter<LocationListAdapter.LocationListViewHolder>() {

    private var layoutInflater: LayoutInflater? = null
    var location_list: JsonArray? = null

    companion object {
        lateinit var itemClick: ItemClick
        private var selectedPosition: Int = 0
    }

    fun setData(location_list: JsonArray, itemClick: ItemClick) {
        this.location_list = location_list
        LocationListAdapter.itemClick = itemClick
    }

    interface ItemClick {
        fun selectLocation(location_item: JsonObject)
    }

    class LocationListViewHolder : RecyclerView.ViewHolder {
        var locationListItemBinding: LocationListItemBinding? = null

        constructor(itemView: LocationListItemBinding) : super(itemView.root) {
            this.locationListItemBinding = itemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationListViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<LocationListItemBinding>(layoutInflater!!, R.layout.location_list_item, parent, false)
        return LocationListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return location_list?.size()!!
    }

    override fun onBindViewHolder(holder: LocationListViewHolder, position: Int) {
        holder.locationListItemBinding!!.locationChk.text = location_list?.get(position)?.asJsonObject?.get("company_name")?.asString+"\n"+location_list?.get(position)?.asJsonObject?.get("address_line_1")?.asString + "\n"+ location_list?.get(position)?.asJsonObject?.get("city")?.asString+"," +location_list?.get(position)?.asJsonObject?.get("postal_code")?.asString

        if (selectedPosition == position)
        {
            holder.locationListItemBinding!!.locationChk.isChecked = true
        } else {
            holder.locationListItemBinding!!.locationChk.isChecked = false
        }

        holder.locationListItemBinding!!.locationChk.setOnCheckedChangeListener { buttonView, isChecked ->
            selectedPosition = position
            itemClick.selectLocation(location_list?.get(position)?.asJsonObject!!)
            GlobalScope.launch(Dispatchers.Main) {
                notifyDataSetChanged()
            }

        }
    }
}