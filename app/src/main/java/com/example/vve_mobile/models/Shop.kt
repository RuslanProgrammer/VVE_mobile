package com.example.vve_mobile.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Shop(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("open_time")
    val openTime: Float,
    @SerializedName("close_time")
    val closeTime: Float,
    val url: String,
    val commonWaitingTime: Int,
    @SerializedName("created_at")
    val createdAt: Date,
    @SerializedName("updated_at")
    val updatedAt: Date
)