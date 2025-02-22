package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses

data class InitiatePayByTransferResponseDTO(
    val expiryDateTime: String,
    val requestId: String,
    val virtualAcctName: String,
    val virtualAcctNo: String
)
