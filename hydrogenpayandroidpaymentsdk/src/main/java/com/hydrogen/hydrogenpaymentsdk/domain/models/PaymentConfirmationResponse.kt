package com.hydrogen.hydrogenpaymentsdk.domain.models

data class PaymentConfirmationResponse(
    val amount: Double,
    val chargedAmount: Double,
    val createdAt: String,
    val currency: String,
    val customerEmail: String,
    val description: String,
    val fees: Double,
    val id: String,
    val narration: String,
    val paidAt: String,
    val paymentType: String,
    val recurringCardToken: String,
    val status: String,
    val transactionRef: String,
    val transactionStatus: String,
    val vat: Double
)
