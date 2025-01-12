package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos

data class PaymentConfirmationDto(
    val amount: Double,
    val authorizationObject: Any?,
    val chargedAmount: Double,
    val createdAt: String,
    val currency: String,
    val customerEmail: String,
    val description: String,
    val fees: Double,
    val id: String,
    val ip: String?,
    val meta: String,
    val metadata: String?,
    val narration: String,
    val paidAt: String,
    val paymentType: String,
    val processorResponse: Any?,
    val recurringCardToken: String,
    val status: String,
    val transactionRef: String,
    val transactionStatus: String,
    val vat: Double
)