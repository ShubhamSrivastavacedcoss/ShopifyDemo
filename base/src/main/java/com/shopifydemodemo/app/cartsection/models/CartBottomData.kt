package com.shopifydemodemo.app.cartsection.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.shopify.graphql.support.ID

class CartBottomData : BaseObservable() {
    var subtotal: String? = null
    var tax: String? = null
    var grandtotal: String? = null

    @get:Bindable
    var subtotaltext: String? = null
        set(subtotaltext) {
            field = subtotaltext
            notifyPropertyChanged(BR.subtotaltext)
        }
    var checkoutId: ID? = null

    var checkouturl: String? = null
    var giftcardID: ID? = null

}
