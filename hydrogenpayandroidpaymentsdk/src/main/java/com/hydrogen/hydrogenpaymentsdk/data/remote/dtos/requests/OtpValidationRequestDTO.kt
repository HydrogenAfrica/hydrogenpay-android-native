package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests

data class OtpValidationRequestDTO(
    val otp: String,
    val providerId: String,
    val transactionId: String
)