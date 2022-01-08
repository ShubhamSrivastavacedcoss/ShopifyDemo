package com.shopifydemodemo.app.homesection.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class Heading : BaseObservable() {
    @get:Bindable
    var heading_one: String? = null
        set(heading_one) {
            field = heading_one
            notifyPropertyChanged(BR.heading_one)
        }
    @get:Bindable
    var heading_two: String? = null
        set(heading_two) {
            field = heading_two
            notifyPropertyChanged(BR.heading_two)
        }
    @get:Bindable
    var heading_three: String? = null
        set(heading_three) {
            field = heading_three
            notifyPropertyChanged(BR.heading_three)
        }
    @get:Bindable
    var heading_four: String? = null
        set(heading_four) {
            field = heading_four
            notifyPropertyChanged(BR.heading_four)
        }
    @get:Bindable
    var heading_five: String? = null
        set(heading_five) {
            field = heading_five
            notifyPropertyChanged(BR.heading_five)
        }
}
