package com.hydrogen.hydrogenpaymentsdk.domain.models

data class PayByTransferResponse(
    val amountPaid: Int,
    val bankName: String,
    val expiryDateTime: String,
    val transactionRef: String,
    val transactionStatus: String,
    val virtualAccountName: String,
    val virtualAccountNo: String
)
