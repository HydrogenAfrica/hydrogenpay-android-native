package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos

data class InitiatePayByTransferResponse(
    val expiryDateTime: String,
    val requestId: String,
    val virtualAcctName: String,
    val virtualAcctNo: String
)
