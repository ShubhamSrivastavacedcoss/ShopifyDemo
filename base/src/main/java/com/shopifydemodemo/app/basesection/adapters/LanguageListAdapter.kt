package com.shopifydemodemo.app.basesection.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.LanguageItemBinding
import kotlinx.android.synthetic.main.language_item.view.*
import javax.inject.Inject

class LanguageListAdapter @Inject constructor() : RecyclerView.Adapter<LanguageListAdapter.LangauageListViewHolder>() {
    class LangauageListViewHolder(itemView: LanguageItemBinding) : RecyclerView.ViewHolder(itemView.root)

    private var languageList: MutableList<String>? = null
    private var languageCallback: LanguageCallback? = null
    private var selectedPosition = -1
    fun setData(languageList: MutableList<String>?, languageCallback: LanguageCallback) {
        this.languageList = languageList
        this.languageCallback = languageCallback
    }

    interface LanguageCallback {
        fun selectedLanguage(language: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LangauageListViewHolder {
        val view = DataBindingUtil.inflate<LanguageItemBinding>(LayoutInflater.from(parent.context), R.layout.language_item, parent, false)
        return LangauageListViewHolder(view)
    }

    override fun onBindViewHolder(holder: LangauageListViewHolder, position: Int) {
        holder.itemView.language_title.text = languageList?.get(position)
        if (selectedPosition == position) {
            holder.itemView.select_language.setImageDrawable(holder.itemView.select_language.context.resources.getDrawable(R.drawable.checked_icon, null))
        } else {
            holder.itemView.select_language.setImageDrawable(holder.itemView.select_language.context.resources.getDrawable(R.drawable.round_circle_selector, null))
        }
        holder.itemView.language_container.setOnClickListener {
            languageCallback?.selectedLanguage(languageList?.get(position) ?: "")
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return languageList?.size ?: 0
    }
}