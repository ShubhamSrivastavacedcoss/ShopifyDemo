package com.shopifydemodemo.app.wishlistsection.viewholders

import androidx.recyclerview.widget.RecyclerView

import com.shopifydemodemo.app.databinding.MWishitemBinding
class WishItem:RecyclerView.ViewHolder{
    var binding:MWishitemBinding
    constructor( binding: MWishitemBinding):super(binding.root){
        this.binding=binding;
    }
}
