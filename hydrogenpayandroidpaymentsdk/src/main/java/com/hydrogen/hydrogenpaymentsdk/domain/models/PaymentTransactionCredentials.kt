package com.hydrogen.hydrogenpaymentsdk.domain.models

data class PaymentTransactionCredentials(
    val transactionRef: String,
    val url: String,
    val transactionId: String,
    val transactionMode: String
)
