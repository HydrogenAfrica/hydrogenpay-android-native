package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos

data class PayByTransferResponseDto(
    val amountPaid: Int,
    val bankName: String,
    val capturedDatetime: String?,
    val completedDatetime: String?,
    val expiryDateTime: String,
    val transactionRef: String,
    val transactionStatus: String,
    val virtualAccountName: String,
    val virtualAccountNo: String
)