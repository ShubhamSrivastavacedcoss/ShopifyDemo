package com.shopifydemodemo.app.dependecyinjection

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Body {
    @SerializedName("queries")
    @Expose
    var queries: List<InnerData>? = null

}