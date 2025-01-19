package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos

data class TransactionDetailsRequest(
    val transactionId: String,
    val isBankTransferTimerRequested: Boolean = true
)