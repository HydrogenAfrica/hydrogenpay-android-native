package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests

data class ResendOTPRequestDto(
    val currency: String,
    val providerId: String,
    val transactionId: String
)