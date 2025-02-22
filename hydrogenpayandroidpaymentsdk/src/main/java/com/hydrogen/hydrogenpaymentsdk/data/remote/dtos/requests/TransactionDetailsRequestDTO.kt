package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests

data class TransactionDetailsRequestDTO(
    val transactionId: String,
    val isBankTransferTimerRequested: Boolean = true
)