package com.shopifydemodemo.app.basesection.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.activities.Splash
import com.shopifydemodemo.app.basesection.models.ListData
import com.shopifydemodemo.app.basesection.viewholders.ListItem
import com.shopifydemodemo.app.databinding.CurrencyListItemBinding
import com.shopifydemodemo.app.utils.Constant
import javax.inject.Inject

class RecylerAdapter @Inject constructor() : RecyclerView.Adapter<ListItem>() {
    private var layoutInflater: LayoutInflater? = null
    private var currencies: List<Storefront.CurrencyCode>? = null
    private var activity: Activity? = null
    fun setData(currencies: List<Storefront.CurrencyCode>, activity: Activity) {
        this.currencies = currencies
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItem {
        val binding = DataBindingUtil.inflate<CurrencyListItemBinding>(LayoutInflater.from(parent.context), R.layout.currency_list_item, parent, false)
        return ListItem(binding)
    }

    override fun onBindViewHolder(holder: ListItem, position: Int) {
        val data = ListData()
        data.textdata = currencies!![position].toString()
        holder.binding.listdata = data
        holder.binding.handler = ClickHandler()
    }

    override fun getItemCount(): Int {
        return currencies!!.size
    }

    inner class ClickHandler {
        fun setCurrency(view: View, data: ListData) {
            (activity as NewBaseActivity).closePopUp()
            val model = (activity as NewBaseActivity).leftMenuViewModel
            model!!.setCurrencyData(data.textdata)
            val intent = Intent(activity, Splash::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity?.startActivity(intent)
            Constant.activityTransition(activity!!)
        }
    }
}
