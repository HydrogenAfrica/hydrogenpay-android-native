package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses

data class OtpValidationResponseDTO(
    val amount: String,
    val errors: List<OTPError>,
    val message: String,
    val otpRetryCount: Int?,
    val panLast4Digits: String,
    val processorResponse: String,
    val responseCode: String,
    val token: String,
    val tokenExpiryDate: String,
    val transactionId: String,
    val transactionIdentifier: String,
    val transactionRef: String
)