package com.shopifydemodemo.app.yotporewards.myrewards.model


import com.google.gson.annotations.SerializedName

data class HistoryItem(
    @SerializedName("action")
    val action: String?,
    @SerializedName("action_name")
    val actionName: Any?,
    @SerializedName("completed_at")
    val completedAt: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("points")
    val points: Int?,
    @SerializedName("status")
    val status: String?
)