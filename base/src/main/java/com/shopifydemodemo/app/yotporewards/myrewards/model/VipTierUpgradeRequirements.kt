package com.shopifydemodemo.app.yotporewards.myrewards.model


import com.google.gson.annotations.SerializedName

data class VipTierUpgradeRequirements(
    @SerializedName("amount_cents_needed")
    val amountCentsNeeded: Int?,
    @SerializedName("amount_cents_needed_in_customer_currency")
    val amountCentsNeededInCustomerCurrency: Int?,
    @SerializedName("campaigns_needed")
    val campaignsNeeded: List<Any>?,
    @SerializedName("points_needed")
    val pointsNeeded: Int?,
    @SerializedName("purchases_needed")
    val purchasesNeeded: Int?,
    @SerializedName("referrals_needed")
    val referralsNeeded: Int?
)