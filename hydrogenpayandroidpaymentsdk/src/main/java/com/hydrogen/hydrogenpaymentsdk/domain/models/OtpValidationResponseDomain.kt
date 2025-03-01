package com.hydrogen.hydrogenpaymentsdk.domain.models

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.OTPError

data class OtpValidationResponseDomain(
    val amount: String,
    val errors: List<OTPError>,
    val message: String,
    val otpRetryCount: Int?,
    val panLast4Digits: String,
    val processorResponse: OTPValidationProcessorResponse,
    val responseCode: String,
    val token: String,
    val tokenExpiryDate: String,
    val transactionId: String,
    val transactionIdentifier: String,
    val transactionRef: String
)