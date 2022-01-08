package com.shopifydemodemo.app.yotporewards.myrewards.model


import com.google.gson.annotations.SerializedName

data class VipTierActionsCompleted(
    @SerializedName("amount_spent_cents")
    val amountSpentCents: Int?,
    @SerializedName("amount_spent_cents_in_customer_currency")
    val amountSpentCentsInCustomerCurrency: Int?,
    @SerializedName("campaigns_completed")
    val campaignsCompleted: List<Any>?,
    @SerializedName("points_earned")
    val pointsEarned: Int?,
    @SerializedName("purchases_made")
    val purchasesMade: Int?,
    @SerializedName("referrals_completed")
    val referralsCompleted: Int?
)