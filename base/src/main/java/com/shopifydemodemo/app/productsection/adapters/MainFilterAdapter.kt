package com.shopifydemodemo.app.productsection.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.FilterItemBinding
import com.shopifydemodemo.app.productsection.activities.FilterActivity

class MainFilterAdapter(
    var listData: ArrayList<String>,
    var tag: String,
    var tagSelectionCallBack: TagSelectionCallBack)
    : RecyclerView.Adapter<MainFilterAdapter.MainFilterViewHolder>() {
    private val TAG = "MainFilterAdapter"

    class MainFilterViewHolder : RecyclerView.ViewHolder {
        var filterItemBinding: FilterItemBinding? = null

        constructor(filterItemBinding: FilterItemBinding) : super(filterItemBinding.root) {
            this.filterItemBinding = filterItemBinding
        }
    }
    interface TagSelectionCallBack {
        fun tagCallback(list: String)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFilterViewHolder {
        var filterdataLayoutBinding = DataBindingUtil.inflate<FilterItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.filter_item, parent, false)
        return MainFilterViewHolder(filterdataLayoutBinding)
    }
    override fun getItemCount(): Int {
        return listData?.size!!
    }
    override fun onBindViewHolder(holder: MainFilterViewHolder, position: Int) {
        holder.filterItemBinding?.filterChk?.text =listData?.get(position)
        Log.i("hashmapval","pos "+listData?.get(position)+" "+tag)
        var list: ArrayList<String> = ArrayList<String>()
        holder.filterItemBinding?.filterChk?.setOnCheckedChangeListener { buttonView, isChecked ->
            var label_tag = tag.toLowerCase().replace(" ","-")+"_"+
                    listData?.get(position).toString().toLowerCase().replace(" ","-")
                        .replace("&","-")
            if (isChecked) {
                FilterActivity.listMap.put(tag,label_tag)
            } else {
                FilterActivity.listMap.remove(tag)
            }
            Log.i("selectedTagslog", "" + FilterActivity.listMap)
            for ((key, value) in FilterActivity.listMap) {
                Log.i("selectedTagslog", "val " + value)
                list.add(value)
            }
            tagSelectionCallBack.tagCallback(list.toString())

        }

    }

}
