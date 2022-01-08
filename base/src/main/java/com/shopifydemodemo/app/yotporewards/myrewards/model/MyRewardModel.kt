package com.shopifydemodemo.app.yotporewards.myrewards.model


import com.google.gson.annotations.SerializedName

data class MyRewardModel(
    @SerializedName("credit_balance")
    val creditBalance: String?,
    @SerializedName("credit_balance_in_customer_currency")
    val creditBalanceInCustomerCurrency: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("has_store_account")
    val hasStoreAccount: Boolean?,
    @SerializedName("history_items")
    val historyItems: List<HistoryItem>?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("last_purchase_at")
    val lastPurchaseAt: Any?,
    @SerializedName("last_seen_at")
    val lastSeenAt: String?,
    @SerializedName("opt_in")
    val optIn: Boolean?,
    @SerializedName("opted_in_at")
    val optedInAt: String?,
    @SerializedName("perks_redeemed")
    val perksRedeemed: Int?,
    @SerializedName("phone_number")
    val phoneNumber: Any?,
    @SerializedName("points_balance")
    val pointsBalance: Int?,
    @SerializedName("points_earned")
    val pointsEarned: Int?,
    @SerializedName("points_expire_at")
    val pointsExpireAt: Any?,
    @SerializedName("pos_account_id")
    val posAccountId: Any?,
    @SerializedName("referral_code")
    val referralCode: ReferralCode?,
    @SerializedName("third_party_id")
    val thirdPartyId: String?,
    @SerializedName("thirty_party_id")
    val thirtyPartyId: String?,
    @SerializedName("total_purchases")
    val totalPurchases: Int?,
    @SerializedName("total_spend_cents")
    val totalSpendCents: Int?,
    @SerializedName("vip_tier_actions_completed")
    val vipTierActionsCompleted: VipTierActionsCompleted?,
    @SerializedName("vip_tier_upgrade_requirements")
    val vipTierUpgradeRequirements: VipTierUpgradeRequirements?
)