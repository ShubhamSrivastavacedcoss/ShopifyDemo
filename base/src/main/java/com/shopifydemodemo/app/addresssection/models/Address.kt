package com.shopifydemodemo.app.addresssection.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.shopify.graphql.support.ID
import java.io.Serializable

class Address : BaseObservable(), Serializable {
    @get:Bindable
    var position: Int = 0
        set(position) {
            field = position
            notifyPropertyChanged(BR.position)
        }

    @get:Bindable
    var firstName: String? = null
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.firstName)
        }

    @get:Bindable
    var lastName: String? = null
        set(lastName) {
            field = lastName
            notifyPropertyChanged(BR.lastName)
        }

    @get:Bindable
    var address1: String? = null
        set(address1) {
            field = address1
            notifyPropertyChanged(BR.address1)
        }

    @get:Bindable
    var address2: String? = null
        set(address2) {
            field = address2
            notifyPropertyChanged(BR.address2)
        }

    @get:Bindable
    var address_id: ID? = null
        set(address_id) {
            field = address_id
            notifyPropertyChanged(BR.address_id)
        }

    @get:Bindable
    var province: String? = null
        set(province) {
            field = province
            notifyPropertyChanged(BR.province)
        }

    @get:Bindable
    var city: String? = null
        set(city) {
            field = city
            notifyPropertyChanged(BR.city)
        }

    @get:Bindable
    var country: String? = null
        set(country) {
            field = country
            notifyPropertyChanged(BR.country)
        }

    @get:Bindable
    var phone: String? = null
        set(phone) {
            field = phone
            notifyPropertyChanged(BR.phone)
        }

    @get:Bindable
    var zip: String? = null
        set(zip) {
            field = zip
            notifyPropertyChanged(BR.zip)
        }
}
