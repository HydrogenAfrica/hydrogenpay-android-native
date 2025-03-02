package com.hydrogen.hydrogenpaymentsdk.domain.models

data class HydrogenPayPaymentTransactionReceipt(
    val amount: String,
    val createdAt: String,
    val paidAt: String,
    val transactionRef: String,
    val paymentType: String,
    val responseDescription: String,
    val transactionResponseCode: String,
    val bank: String,
    val processorResponse: String?,
    val recurringCardToken: String?,
    val accountName: String?,
    val accountNumber: String?,
    val fees: String = "0",
    val vat: String = "0"
)