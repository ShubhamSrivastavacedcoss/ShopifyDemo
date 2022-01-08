package com.shopifydemodemo.app.userprofilesection.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class User : BaseObservable() {
    @get:Bindable
    var firstname: String? = null
        set(firstname) {
            field = firstname
            notifyPropertyChanged(BR.firstname)
        }
    @get:Bindable
    var lastname: String? = null
        set(lastname) {
            field = lastname
            notifyPropertyChanged(BR.lastname)
        }
    @get:Bindable
    var email: String? = null
        set(email) {
            field = email
            notifyPropertyChanged(BR.email)
        }
    @get:Bindable
    var password: String? = null
        set(password) {
            field = password
            notifyPropertyChanged(BR.password)
        }
}
