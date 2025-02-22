package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models

data class CardDetails(
    val cardNumber: String,
    val cvv: Int,
    val expiryMonth: Int,
    val expiryYear: Int,
    val id: String,
    val pin: String,
    val scheme: Int
)