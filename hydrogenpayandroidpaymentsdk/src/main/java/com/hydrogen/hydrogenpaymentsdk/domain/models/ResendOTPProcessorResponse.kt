package com.hydrogen.hydrogenpaymentsdk.domain.models

data class ResendOTPProcessorResponse(
    val amount: String,
    val errors: Any,
    val message: String,
    val paymentId: String,
    val plainTextSupportMessage: String?,
    val responseCode: String,
    val transactionId: String?,
    val transactionRef: String
)