package com.shopifydemodemo.app.yotporewards.earnrewards.model


import com.google.gson.annotations.SerializedName

data class EarnRewardModelItem(
    @SerializedName("action_name")
    val actionName: Any?,
    @SerializedName("ask_year")
    val askYear: Boolean?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("cta_text")
    val ctaText: String?,
    @SerializedName("default_email_body")
    val defaultEmailBody: String?,
    @SerializedName("details")
    val details: String?,
    @SerializedName("display_order")
    val displayOrder: Int?,
    @SerializedName("entity_id")
    val entityId: Any?,
    @SerializedName("expires_at")
    val expiresAt: Any?,
    @SerializedName("extra_copy1")
    val extraCopy1: Any?,
    @SerializedName("extra_copy2")
    val extraCopy2: Any?,
    @SerializedName("hashtags")
    val hashtags: Any?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("max_completions_per_user")
    val maxCompletionsPerUser: Int?,
    @SerializedName("min_actions_required")
    val minActionsRequired: Int?,
    @SerializedName("question")
    val question: Any?,
    @SerializedName("reward_text")
    val rewardText: String?,
    @SerializedName("share_text")
    val shareText: Any?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("unrendered_details")
    val unrenderedDetails: String?,
    @SerializedName("unrendered_title")
    val unrenderedTitle: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("url")
    val url: Any?,
    @SerializedName("username")
    val username: Any?
)