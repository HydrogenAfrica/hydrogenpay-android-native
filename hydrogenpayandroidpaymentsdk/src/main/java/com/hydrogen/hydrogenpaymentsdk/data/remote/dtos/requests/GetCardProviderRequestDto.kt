package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests

data class GetCardProviderRequestDto(
    val CardDetails: String,
    val transactionId: String
)