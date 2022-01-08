package com.shopifydemodemo.app.yotporewards.myrewards.model


import com.google.gson.annotations.SerializedName

data class ReferralCode(
    @SerializedName("amount_cents")
    val amountCents: Int?,
    @SerializedName("average_amount_cents")
    val averageAmountCents: Int?,
    @SerializedName("code")
    val code: String?,
    @SerializedName("completed_referral_customers")
    val completedReferralCustomers: List<Any>?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("email_shares")
    val emailShares: Int?,
    @SerializedName("emails_sent")
    val emailsSent: Int?,
    @SerializedName("emails_viewed")
    val emailsViewed: Int?,
    @SerializedName("expired")
    val expired: Boolean?,
    @SerializedName("expires_at")
    val expiresAt: Any?,
    @SerializedName("facebook_shares")
    val facebookShares: Int?,
    @SerializedName("links_clicked_from_email")
    val linksClickedFromEmail: Int?,
    @SerializedName("links_clicked_from_facebook")
    val linksClickedFromFacebook: Int?,
    @SerializedName("links_clicked_from_twitter")
    val linksClickedFromTwitter: Int?,
    @SerializedName("orders")
    val orders: Int?,
    @SerializedName("shares")
    val shares: Int?,
    @SerializedName("total_clicks")
    val totalClicks: Int?,
    @SerializedName("twitter_shares")
    val twitterShares: Int?,
    @SerializedName("unique_clicks")
    val uniqueClicks: Int?
)