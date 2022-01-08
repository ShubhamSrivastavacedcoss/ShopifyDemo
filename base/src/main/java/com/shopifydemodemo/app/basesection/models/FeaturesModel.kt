package com.shopifydemodemo.app.basesection.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class FeaturesModel : BaseObservable() {
    @Bindable
    var zapietEnable: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.zapietEnable)
        }

    @Bindable
    var socialloginEnable: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.socialloginEnable)
        }

    @Bindable
    var filterEnable: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.filterEnable)
        }

    @Bindable
    var localpickupEnable: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.localpickupEnable)
        }

    @Bindable
    var smileIO: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.smileIO)
        }

    @Bindable
    var appOnlyDiscount: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.appOnlyDiscount)
        }

    @Bindable
    var whatsappChat: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.whatsappChat)
        }

    @Bindable
    var zenDeskChat: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.zenDeskChat)
        }

    @Bindable
    var fbMessenger: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.fbMessenger)
        }

    @Bindable
    var tidioChat: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.tidioChat)
        }

    @Bindable
    var yoptoLoyalty: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.yoptoLoyalty)
        }

    @Bindable
    var forceUpdate: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.forceUpdate)
        }

    @Bindable
    var productListEnabled: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.productListEnabled)
        }

    @Bindable
    var firebaseEvents: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.firebaseEvents)
        }

    @Bindable
    var aliReviews: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.aliReviews)
        }

    @Bindable
    var nativeOrderView: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.nativeOrderView)
        }

    @Bindable
    var recommendedProducts: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.recommendedProducts)
        }

    @Bindable
    var reOrderEnabled: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.reOrderEnabled)
        }

    @Bindable
    var addCartEnabled: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.addCartEnabled)
        }

    @Bindable
    var sizeChartVisibility: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.sizeChartVisibility)
        }

    @Bindable
    var productReview: Boolean? = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.productReview)
        }


    @Bindable
    var outOfStock: Boolean? = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.outOfStock)
        }

    @Bindable
    var showBottomNavigation: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showBottomNavigation)
        }

    @Bindable
    var judgemeProductReview: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.judgemeProductReview)
        }

    @Bindable
    var in_app_wishlist: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.in_app_wishlist)
        }

    @Bindable
    var rtl_support: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.rtl_support)
        }


    @Bindable
    var product_share: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.product_share)
        }

    @Bindable
    var multi_currency: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.multi_currency)
        }

    @Bindable
    var multi_language: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.multi_language)
        }

    @Bindable
    var abandoned_cart_compaigns: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.abandoned_cart_compaigns)
        }

    @Bindable
    var ai_product_reccomendaton: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.ai_product_reccomendaton)
        }

    @Bindable
    var qr_code_search_scanner: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.qr_code_search_scanner)
        }

    @Bindable
    var ardumented_reality: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.ardumented_reality)
        }
}