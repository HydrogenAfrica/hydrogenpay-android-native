package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses

data class ResendOTPResponseDto(
    val `data`: ResendOTPResponseData,
    val message: String,
    val statusCode: String
)