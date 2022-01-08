package com.shopifydemodemo.app.yotporewards.getrewards.model


import com.google.gson.annotations.SerializedName

data class GetRewardModelItem(
    @SerializedName("amount")
    val amount: Int?,
    @SerializedName("applies_to_product_type")
    val appliesToProductType: String?,
    @SerializedName("cost_text")
    val costText: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("discount_amount_cents")
    val discountAmountCents: Int?,
    @SerializedName("discount_percentage")
    val discountPercentage: Int?,
    @SerializedName("discount_rate_cents")
    val discountRateCents: Int?,
    @SerializedName("discount_type")
    val discountType: String?,
    @SerializedName("discount_value_cents")
    val discountValueCents: Any?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("unrendered_description")
    val unrenderedDescription: String?,
    @SerializedName("unrendered_name")
    val unrenderedName: String?
)