package com.shopifydemodemo.app.productsection.viewholders
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.databinding.MPersonalisedBinding
import com.shopifydemodemo.app.databinding.MProductitemBinding
class ProductItem :RecyclerView.ViewHolder{
    var binding: MProductitemBinding?=null
    var personalbinding: MPersonalisedBinding?=null
    constructor(binding: MProductitemBinding ) : super(binding.root) {
        this.binding=binding
    }
    constructor(personalbinding: MPersonalisedBinding ) : super(personalbinding.root) {
        this.personalbinding=personalbinding
    }
}

