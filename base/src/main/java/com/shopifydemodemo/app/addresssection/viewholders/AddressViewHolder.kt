package com.shopifydemodemo.app.addresssection.viewholders
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.databinding.MAddressitemBinding
class AddressViewHolder :RecyclerView.ViewHolder{
    var binding: MAddressitemBinding
    constructor(binding: MAddressitemBinding) : super(binding.root) {
        this.binding=binding;
    }
}

