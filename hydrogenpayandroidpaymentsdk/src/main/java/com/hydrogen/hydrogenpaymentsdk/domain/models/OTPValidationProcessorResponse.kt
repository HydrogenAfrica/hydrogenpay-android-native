package com.hydrogen.hydrogenpaymentsdk.domain.models

data class OTPValidationProcessorResponse(
    val amount: String,
    val cardType: String,
    val errors: List<OTPValidationProcessorError>?,
    val message: String,
    val panLast4Digits: String,
    val responseCode: String,
    val token: String,
    val tokenExpiryDate: String,
    val transactionIdentifier: String,
    val transactionRef: String
)