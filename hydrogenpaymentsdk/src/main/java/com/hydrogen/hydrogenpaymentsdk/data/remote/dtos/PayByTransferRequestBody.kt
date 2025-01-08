package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos

data class PayByTransferRequestBody(
    val amount: Int,
    val customerName: String,
    val email: String,
    val transactionRef: String?,
    val currency: String?,
    val description: String?,
    val callback: String?,
    val meta: String?
)