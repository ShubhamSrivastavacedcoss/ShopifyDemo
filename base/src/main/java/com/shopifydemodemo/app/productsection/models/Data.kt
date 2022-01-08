package com.shopifydemodemo.app.productsection.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Data(
    @SerializedName("reviews")
    val reviews: List<Review>?
):Serializable