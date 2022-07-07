package com.example.vve_mobile.models

import com.google.gson.annotations.SerializedName
import java.util.*

abstract class User {
    abstract val id: Int
    abstract val name: String
    abstract val surname: String
    abstract val email: String
}

data class Administrator(
    override val id: Int,
    override val name: String,
    override val surname: String,
    override val email: String,
    val shop: Int,
) : User()

data class Worker(
    override val id: Int,
    override val name: String,
    override val surname: String,
    override val email: String,
    val shop: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
) : User()

data class Customer(
    override val id: Int,
    override val name: String,
    override val surname: String,
    override val email: String,
) : User()

data class CheckoutHistory(
    val id: Int,
    val checkout: Int,
    val time: String,
    @SerializedName("max_load")
    val maxLoad: String,
    @SerializedName("cur_load")
    val curLoad: String,
)

data class Reservation(
    val id: Int,
    val shop: Int,
    @SerializedName("start_time")
    val startTime: String,
    val customer: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)