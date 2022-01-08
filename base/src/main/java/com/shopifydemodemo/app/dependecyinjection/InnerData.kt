package com.shopifydemodemo.app.dependecyinjection

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InnerData {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("recommendation_type")
    @Expose
    var recommendationType: String? = null

    @SerializedName("max_recommendations")
    @Expose
    var maxRecommendations: Int? = null

    @SerializedName("product_ids")
    @Expose
    var productIds: List<Long>? = null

}