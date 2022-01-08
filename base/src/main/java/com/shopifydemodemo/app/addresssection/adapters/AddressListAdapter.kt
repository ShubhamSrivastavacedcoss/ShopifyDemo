package com.shopifydemodemo.app.addresssection.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.addresssection.models.Address
import com.shopifydemodemo.app.addresssection.viewholders.AddressViewHolder
import com.shopifydemodemo.app.addresssection.viewmodels.AddressModel
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MAddressitemBinding
import javax.inject.Inject
class AddressListAdapter @Inject
constructor() : RecyclerView.Adapter<AddressViewHolder>() {
    var data: MutableList<Storefront.MailingAddressEdge>? = null
    private var layoutInflater: LayoutInflater? = null
    private var model: AddressModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = DataBindingUtil.inflate<MAddressitemBinding>(LayoutInflater.from(parent.context), R.layout.m_addressitem, parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = Address()
        address.position = position
        address.address_id = data?.get(position)?.node?.id
        address.firstName =data?.get(position)?.node?.firstName
        address.lastName =data?.get(position)?.node?.lastName
        if (!data?.get(position)?.node?.address1?.isEmpty()!!) {
            address.address1 =data?.get(position)?.node?.address1
            holder.binding.address1.visibility = View.VISIBLE
        }
        if (!data?.get(position)?.node?.address2?.isEmpty()!!) {
            address.address2 =data?.get(position)?.node?.address2
            holder.binding.address2.visibility = View.VISIBLE
        }
        address.city =data?.get(position)?.node?.city
        address.country =data?.get(position)?.node?.country
        address.phone =data?.get(position)?.node?.phone
        address.zip =data?.get(position)?.node?.zip
        address.province =data?.get(position)?.node?.province
        holder.binding.handler = ClickHandler()
        holder.binding.address = address
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    fun setData(data: MutableList<Storefront.MailingAddressEdge>, model: AddressModel?) {
        this.data = data
        this.model = model
    }

    inner class ClickHandler {
        fun deleteAddress(view: View, address: Address) {
            model!!.deleteAddress(view.resources.getString(R.string.deleteaddress), address)
            data!!.removeAt(address.position)
            notifyItemRemoved(address.position)
            notifyItemRangeChanged(address.position, data!!.size)
        }
        fun editAddress(view: View, address: Address) {
            model!!.setSheet()
            model!!.setAddress(address)
        }
    }
}
