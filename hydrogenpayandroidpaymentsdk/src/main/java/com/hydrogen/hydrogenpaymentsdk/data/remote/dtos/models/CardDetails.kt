package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models

data class CardDetails(
    val cardNumber: String,
    val cvv: String,
    val expiryMonth: String,
    val expiryYear: String,
    val id: String,
    val pin: String,
    val scheme: Int
)