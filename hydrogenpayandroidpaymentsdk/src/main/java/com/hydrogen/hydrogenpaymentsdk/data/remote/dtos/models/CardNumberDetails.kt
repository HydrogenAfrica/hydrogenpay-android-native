package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models

data class CardNumberDetails(
    val cardNumber: String,
    val id: String,
    val isCheckDiscount: Boolean = true
)